package com.seotrove.demo.v1;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XmlValidator {
    public static void validate(Document doc, File xsdFile) throws Exception {

        System.setProperty(
                "javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
                "net.sf.saxon.jaxp.SchemaFactoryImpl"
        );
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsdFile);
        Validator validator = schema.newValidator();
        try {
            validator.validate(new DOMSource(doc));
            System.out.println("\n✅ XML is valid against schema");
        } catch (Exception e) {
            System.err.println("\n❌ Validation failed: " + e.getMessage());
            throw e;
        }
    }
}
