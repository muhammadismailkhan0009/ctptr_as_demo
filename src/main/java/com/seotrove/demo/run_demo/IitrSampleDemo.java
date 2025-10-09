package com.seotrove.demo.run_demo;

import com.seotrove.demo.v1.XmlValidator;
import com.seotrove.demo.v5_current.DynamicXmlGenerator;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class IitrSampleDemo {

    public static void main(String[] args) throws Exception {
        // --- CONFIGURATION ---
        String xsdPath = "src/main/resources/schemas/incdtls.xsd";
        String outputPath = "library_output.xml";

        // Define the data to be populated using fully qualified paths.
        // The generator will create the structure needed to set these values.
        Map<String, Object> data = new LinkedHashMap<>();


        data.put("INCDTLS.Rp.LodgmentPeriodStartD", "2024-11-11");
        data.put("INCDTLS.Rp.LodgmentPeriodEndD", "2024-11-11");
        data.put("INCDTLS.Rp.TaxFileNumberId", "11223378");

        data.put("INCDTLS.Rp.EmployeeShareSchemeCollection.EmployeeShareScheme[0].OrganisationNameDetailsOrganisationalNameT", "name");
        data.put("INCDTLS.Rp.EmployeeShareSchemeCollection.EmployeeShareScheme[1].OrganisationNameDetailsOrganisationalNameT", "name2");


        // --- EXECUTION ---
        DynamicXmlGenerator generator = new DynamicXmlGenerator();
        generator.generateXml(xsdPath, outputPath, data);

        System.out.println("XML generation complete. Output file: " + outputPath);

        //        validate
        File xmlFile = new File("library_output.xml");
        File xsdFile = new File(xsdPath);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(xmlFile);

        XmlValidator.validate(doc, xsdFile);
    }
}
