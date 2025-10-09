package com.seotrove.demo.v3;

import org.eclipse.xsd.XSDSchema;

import java.io.File;

public class JsonXmlGeneratorApp {

    public static void main(String[] args) throws Exception {
        File xsdFile = new File("src/main/resources/ato.as.0004.2025.submit.01.00.xsd");

        // 1. Load schema
        XSDSchema schema = EclipseXsdLoader.loadSchema(xsdFile);

        // 2. Print global root elements

        SchemaWalker.printSchema(schema);
        // 🚀 Next step: generate XML
        // Option A: Iterate over element declarations (manual XML builder)
        // Option B: Use EMF SampleInstanceGenerator (auto skeleton XML)
//        option 1:
    }
}
