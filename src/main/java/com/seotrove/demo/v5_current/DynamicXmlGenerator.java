package com.seotrove.demo.v5_current;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicXmlGenerator {

    // Regex to parse a path segment like "Book[1]" into name ("Book") and index ("1")
    private static final Pattern SEGMENT_PATTERN = Pattern.compile("([^\\[\\]]+)(?:\\[(\\d+)\\])?");

    private EPackage ePackage;
    private EFactory eFactory;

    private ResourceSet rs;
    private ExtendedMetaData extendedMetaData;



    /**
     * Generates an XML file from an XSD and a map of data.
     *
     * @param xsdPath    Path to the XSD schema file.
     * @param outputPath Path for the generated XML file.
     * @param data       Map of path-to-value data to populate.
     */
    public void generateXml(String xsdPath, String outputPath, Map<String, Object> data) throws Exception {
        // Initialize EMF and load the XSD to create a dynamic Ecore model in memory
        initialize(xsdPath);

        // Step 2: find DocumentRoot and create root element
        EClass documentRootClass = (EClass) ePackage.getEClassifier("DocumentRoot");
        if (documentRootClass == null)
            throw new IllegalStateException("No DocumentRoot found in EPackage.");

        EObject documentRoot = eFactory.create(documentRootClass);

        // The first segment of your path ("ASSubmitRequest") decides which global element to set
        String rootElementName = data.keySet().iterator().next().split("\\.")[0];
        EStructuralFeature rootFeature = findFeature(documentRootClass, rootElementName);

        // Create the main root element (like ASSubmitRequest)
        EClass rootClass = (EClass) ((EReference) rootFeature).getEType();
        EObject rootObject = eFactory.create(rootClass);
        documentRoot.eSet(rootFeature, rootObject);

        // Step 3: fill the structure (skip the root name in each path)
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String pathWithoutRoot = entry.getKey().replaceFirst("^" + rootElementName + "\\.", "");
            setValueByPath(rootObject, pathWithoutRoot, entry.getValue());
        }

        // Save the populated EObject model to an XML file
        save(rootObject, outputPath);

    }

    public void initialize(String xsdPath) {
        // 1) Global factory registrations
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        reg.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
        reg.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        reg.getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());

        // 2) ResourceSet + ExtendedMetaData
        rs = new ResourceSetImpl();
        extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());

        // 3) Build Ecore from the XSD (use EMF URI)
        XSDEcoreBuilder builder = new XSDEcoreBuilder(extendedMetaData);
        URI xsdURI = URI.createFileURI(new File(xsdPath).getAbsolutePath());
        Collection<EObject> generated = builder.generate(xsdURI);

        // 4) Collect and register all EPackages; choose the one you’ll use
        for (EObject eo : generated) {
            if (eo instanceof EPackage pkg) {
                rs.getPackageRegistry().put(pkg.getNsURI(), pkg);
                if (ePackage == null) {
                    ePackage = pkg; // pick the first, or filter by nsURI you expect
                }
            }
        }
        if (ePackage == null)
            throw new IllegalStateException("No EPackage generated from XSD.");
        eFactory = ePackage.getEFactoryInstance();
    }

    /**
     * The core method that navigates or creates the structure for a given path and sets the value.
     *
     * @param root  The starting EObject.
     * @param path  The dot-separated path string.
     * @param value The value to set at the end of the path.
     */
    public void setValueByPath(EObject root, String path, Object value) {
        String[] segments = path.split("\\.");
        EObject currentObject = root;

        // Navigate/create until the second to last segment
        for (int i = 0; i < segments.length - 1; i++) {
            currentObject = getOrCreateNextEObject(currentObject, segments[i]);
        }

        // Set the final value on the last segment
        setFinalValue(currentObject, segments[segments.length - 1], value);
    }

    /**
     * Gets or creates the next EObject in the path.
     */
    private EObject getOrCreateNextEObject(EObject currentObject, String segment) {
        Matcher matcher = SEGMENT_PATTERN.matcher(segment);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid path segment: " + segment);
        }

        String name = matcher.group(1);
        String indexStr = matcher.group(2);
        int index = (indexStr != null) ? Integer.parseInt(indexStr) : 0;

        // Find the feature (child element definition) on the current EObject
        EStructuralFeature feature = findFeature(currentObject.eClass(), name);
        if (!(feature instanceof EReference && ((EReference) feature).isContainment())) {
            throw new IllegalArgumentException("Path segment is not a containment reference: " + name);
        }

        if (feature.isMany()) {
            // Handle lists (maxOccurs > 1)
            @SuppressWarnings("unchecked")
            List<EObject> list = (List<EObject>) currentObject.eGet(feature);
            // Grow the list if the required index is not yet present
            while (list.size() <= index) {
                list.add(eFactory.create((EClass) feature.getEType()));
            }
            return list.get(index);
        } else {
            // Handle single objects
            EObject child = (EObject) currentObject.eGet(feature);
            if (child == null) {
                child = eFactory.create((EClass) feature.getEType());
                currentObject.eSet(feature, child);
            }
            return child;
        }
    }

    /**
     * Sets the final value, which must be an attribute or a simple text element.
     */
    private void setFinalValue(EObject targetObject, String segment, Object value) {
        EStructuralFeature feature = findFeature(targetObject.eClass(), segment);
        if (!(feature instanceof EAttribute)) {
            throw new IllegalArgumentException("Final path segment is not an attribute or simple element: " + segment);
        }

        // Convert the value to the correct type for the attribute if necessary
        EDataType attributeType = (EDataType) feature.getEType();
        Object convertedValue = eFactory.createFromString(attributeType, value.toString());

        targetObject.eSet(feature, convertedValue);
    }

    /**
     * Finds a feature (element or attribute) by its name, case-insensitively.
     * The XSD element/attribute names are often capitalized differently than Ecore feature names.
     */
    private EStructuralFeature findFeature(EClass eClass, String featureName) {
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Feature '" + featureName + "' not found in EClass '" + eClass.getName() + "'");
    }


    /**
     * Saves the EObject tree to an XML file.
     */
    private void save(EObject root, String outputPath) throws IOException {
        Resource resource = rs.createResource(URI.createFileURI(outputPath));
        resource.getContents().add(root);

        Map<Object, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.FALSE); // ✅ crucial
        options.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
        options.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);

//        ((XMLResource) resource).getDefaultSaveOptions().put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);

        resource.save(options);
    }
}