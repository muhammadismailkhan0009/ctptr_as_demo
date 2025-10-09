package com.seotrove.demo.v3;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;

import java.io.File;
import java.util.List;

public class EclipseXsdLoader {

    public static XSDSchema loadSchema(File xsdFile) throws Exception {
        ResourceSetImpl resourceSet = new ResourceSetImpl();

        // Register default XSD resource factory
        resourceSet.getResourceFactoryRegistry()
                .getExtensionToFactoryMap()
                .put("xsd", new XSDResourceFactoryImpl());

        // Load schema (resolves imports/includes transitively)
        URI uri = URI.createFileURI(xsdFile.getAbsolutePath());
        Resource resource = resourceSet.getResource(uri, true);

        if (resource.getContents().isEmpty()) {
            throw new RuntimeException("Schema load failed: no contents");
        }

        XSDSchema schema = (XSDSchema) resource.getContents().get(0);

        // Print diagnostics (if any warnings/errors while parsing)
        List<XSDDiagnostic> diags = schema.getDiagnostics();
        for (XSDDiagnostic diag : diags) {
            System.out.println("⚠️ Schema Diagnostic: " + diag.getMessage());
        }

        return schema;
    }
}
