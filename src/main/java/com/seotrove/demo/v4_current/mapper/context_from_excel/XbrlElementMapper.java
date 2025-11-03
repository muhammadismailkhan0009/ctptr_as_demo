package com.seotrove.demo.v4_current.mapper.context_from_excel;

import com.seotrove.demo.v4_current.annotations.v2.XbrlContextIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.XbrlElementIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.domain.ContextDataModel;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xbrl.x2003.instance.XbrlDocument;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Core runtime mapper that builds an XBRL document by:
 * 1. Building contexts (via @XbrlContextField)
 * 2. Building elements (via @XbrlElementField)
 * <p>
 * All class and field linkages are resolved via reflection and annotation metadata.
 */
public final class XbrlElementMapper {

    /**
     * Builds an entire XBRL instance document from any annotated DTO instance.
     */

    Map<String, ContextDataModel> contextDataMap = new HashMap<>();
    Map<String, ContextRow> contextRowMapFromXml = new HashMap<>();
    List<XmlObject> elements = new ArrayList<>();

    private void addContext(XbrlDocument.Xbrl xbrl) {
        for (String label : contextDataMap.keySet()) {
            var rowMap = contextRowMapFromXml.get(label);
            var data = contextDataMap.get(label);
            GenericContextBuilder.build(xbrl, rowMap, data);
        }
    }

    private static ContextRow readExcelFile(String label) {
        try {
            return ExcelFileReader.readContextFromExcel(new File("src/main/resources/iitr.xlsx"),
                    label);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void map(List<Object> dtos, XbrlDocument.Xbrl xbrl) {
        Objects.requireNonNull(dtos, "DTO instance cannot be null");

        try {
            // Create the root XBRL document

            for (Object dto : dtos) {

                // === Phase 1: Handle Context Fields ===
                for (Field field : dto.getClass().getDeclaredFields()) {
                    XbrlContextIdentifier ctxAnn = field.getAnnotation(XbrlContextIdentifier.class);
                    if (ctxAnn == null) continue;

                    field.setAccessible(true);
                    Object value = field.get(dto);
                    if (value == null) continue;

                    // Create or reuse cached context instance
                    var contextLabel = ctxAnn.label();
                    var contextField = ctxAnn.field();
                    /**
                     * 1- get row from excel for context label if not found and cache it
                     *
                     */
                    var existingRow = contextRowMapFromXml.get(contextLabel);
                    if (existingRow == null) {
                        existingRow = readExcelFile(contextLabel);
                        contextRowMapFromXml.put(contextLabel, existingRow);
                    }


//                    2- populate context data from dtos and cache it
                    var existingContextData = contextDataMap.get(contextLabel);
                    if (existingContextData == null) {
                        existingContextData = new ContextDataModel();
                    }
                    existingContextData.addData(contextLabel, contextField, value);
                    contextDataMap.put(contextLabel, existingContextData);
                }

                // === Phase 2: Handle Element Fields ===
                for (Field field : dto.getClass().getDeclaredFields()) {
                    XbrlElementIdentifier elAnn = field.getAnnotation(XbrlElementIdentifier.class);
                    if (elAnn == null) continue;

                    field.setAccessible(true);
                    Object value = field.get(dto);
                    if (value == null) continue;

                    Class<?> docClass = elAnn.target();
                    System.out.println("🔧 Processing: " + field.getName() + " → " + docClass.getSimpleName());

                    // ✅ Use reflection to access static Factory field
                    Field factoryField = docClass.getField("Factory");
                    Object factoryInstance = factoryField.get(null); // static field

                    // ✅ Call factory.newInstance()
                    Method newInstanceMethod = factoryInstance.getClass().getMethod("newInstance");
                    Object docInstance = newInstanceMethod.invoke(factoryInstance);

                    // ✅ Call addNew...() method to get inner element
                    String addNewMethodName = "addNew" + docClass.getSimpleName().replace("Document", "");
                    Method addNewMethod = docClass.getMethod(addNewMethodName);
                    Object elementInstance = addNewMethod.invoke(docInstance);

                    // ✅ Set value

                    try {
                        Method m = elementInstance.getClass().getMethod("setStringValue", String.class);
                        m.invoke(elementInstance, value.toString());
                    } catch (NoSuchMethodException e) {
                        System.err.println("❌ No suitable setter for: " + elementInstance.getClass().getName());
                    }

                    // ✅ Optional: set contextRef
                    try {
                        // Get the static field's value (pass null for static fields)
                        Method contextMethod = elementInstance.getClass().getMethod("setContextRef", String.class);
                        contextMethod.invoke(elementInstance, elAnn.contextRef());
                    } catch (NoSuchMethodException ignored) {
                    }

                    // ✅ Optional: set unitRef
                    if (!elAnn.unitRef().isEmpty()) {
                        try {
                            // Get the static field's value (pass null for static fields)
                            Method contextMethod = elementInstance.getClass().getMethod("setUnitRef", String.class);
                            contextMethod.invoke(elementInstance, elAnn.unitRef());
                        } catch (NoSuchMethodException ignored) {
                        }
                    }
                    elements.add((XmlObject) docInstance);
                }

            }
            addContext(xbrl);
            elements.parallelStream().forEach(element -> addElementToXMl(element, xbrl));

        } catch (Exception e) {
            throw new RuntimeException("Error mapping XBRL DTO", e);
        }
    }

    public static void addElementToXMl(XmlObject element, XbrlDocument.Xbrl xbrl) {
        try (XmlCursor target = xbrl.newCursor(); XmlCursor source = element.newCursor()) {
            target.toEndToken();  // move just before </xbrl>
            source.toFirstChild(); // move to actual payload element
            source.copyXml(target); // copy safely
        }
    }

    public static XmlOptions setOptions() {
        XmlOptions opts = new XmlOptions();
        opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        opts.setValidateStrict();

        return opts;
    }
}


