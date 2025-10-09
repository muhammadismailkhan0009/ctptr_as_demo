package com.seotrove.demo.run_demo;

import com.seotrove.demo.v1.XmlValidator;
import com.seotrove.demo.v5_current.DynamicXmlGenerator;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class AsSubmitDemo {
    public static void main(String[] args) throws Exception {
        // --- CONFIGURATION ---
        String xsdPath = "src/main/resources/schemas/ato.as.0004.2025.submit.01.00.xsd";
        String outputPath = "library_output.xml";

        // Define the data to be populated using fully qualified paths.
        // The generator will create the structure needed to set these values.
        Map<String, Object> data = new LinkedHashMap<>();
//        RP module
        data.put("ASSubmitRequest.RP.AustralianBusinessNumberId", "12345678901");
        data.put("ASSubmitRequest.RP.TaxFileNumberId", "123456789");
        data.put("ASSubmitRequest.RP.PartyEntityNameT", "name");
        data.put("ASSubmitRequest.RP.BusinessDocumentGovernmentGeneratedId", "123456789");
        data.put("ASSubmitRequest.RP.TypeC", "A");
        data.put("ASSubmitRequest.RP.StatementRevisionI", false);

//        declaration module
        data.put("ASSubmitRequest.INT.Declaration.StatementAcceptedI", false);
        data.put("ASSubmitRequest.INT.Declaration.SignatoryIdentifierT", false);

        data.put("ASSubmitRequest.INT.AustralianBusinessNumberId", "12345678911");







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
