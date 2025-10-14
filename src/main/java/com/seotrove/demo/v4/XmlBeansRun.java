package com.seotrove.demo.v4;


import au.gov.sbr.ato.iitr.IdentifiersAustralianBusinessNumberIdentifierDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x1999.xlink.TypeAttribute;
import org.xbrl.x2003.instance.XbrlDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlBeansRun {

    public static void main(String[] args) throws Exception {
      var xbrlDocument = createXbrlDocument();
      var xbrl = createXbrl(xbrlDocument);
      var options = setOptions();

      addIntermediaryDetails(xbrl);
      save("sample_iitr.xml", xbrlDocument, options);

      validate(xbrl, options);

    }

    public static void addIntermediaryDetails(XbrlDocument.Xbrl xbrl) {
        var businessNumberDoc = IdentifiersAustralianBusinessNumberIdentifierDocument.Factory.newInstance();

        var businessNumber = businessNumberDoc.addNewIdentifiersAustralianBusinessNumberIdentifier();
        businessNumber.setStringValue("11111111111");
        businessNumber.setContextRef("intermediary_context");
        addElementToXMl(businessNumberDoc,xbrl);
    }

    public static void save(String outputFilePath, XbrlDocument xbrlDocument, XmlOptions opts) throws Exception {
        xbrlDocument.save(new File(outputFilePath), opts);
    }


    public static XmlOptions setOptions(){
        XmlOptions opts = new XmlOptions();
        opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        opts.setValidateStrict();

        return opts;
    }
    public static void addElementToXMl(XmlObject element, XbrlDocument.Xbrl xbrl) {
        try (XmlCursor rootCursor = xbrl.newCursor();
             XmlCursor famCursor = element.newCursor()) {

            famCursor.toFirstChild();

            rootCursor.toEndToken(); // move inside <xbrl>
            famCursor.copyXml(rootCursor); // copy element subtree
        }
    }

    public static void validate(XbrlDocument.Xbrl xbrl, XmlOptions options) throws Exception {

        // Validation error listener
        List<XmlError> errors = new ArrayList<>();
        options.setErrorListener(errors);

        if (!xbrl.validate(options)) {
            System.out.println("❌ Validation failed for XBRL root:");
            for (XmlError err : errors) {
                System.out.println("  - " + err );
            }
            return; // Stop if invalid
        }
    }

    public static XbrlDocument.Xbrl createXbrl(XbrlDocument xbrlDoc) throws Exception {
        var xbrl = xbrlDoc.addNewXbrl();
       var schemaRef = xbrl.addNewSchemaRef();
       schemaRef.setType(TypeAttribute.Type.Enum.forString("simple"));
       schemaRef.setHref("a.com");

        return xbrl;
    }
    public static XbrlDocument createXbrlDocument(){
      var  xbrl = XbrlDocument.Factory.newInstance();

      return xbrl;
    }
}
