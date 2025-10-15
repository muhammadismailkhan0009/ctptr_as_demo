package com.seotrove.demo.v4;


import au.gov.sbr.ato.iitr.IdentifiersAustralianBusinessNumberIdentifierDocument;
import au.gov.sbr.ato.iitr.IncomeTaxDeductionGiftDonationAmountDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x1999.xlink.TypeAttribute;
import org.xbrl.x2003.instance.XbrlDocument;
import org.xbrl.x2006.xbrldi.ExplicitMemberDocument;

import javax.xml.namespace.QName;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class XmlBeansRun {

    public static void main(String[] args) throws Exception {
        var xbrlDocument = createXbrlDocument();
        var xbrl = createXbrl(xbrlDocument);
        var options = setOptions();

        addContext(xbrl, "intermediary_context", null);
        addIntermediaryDetails(xbrl, options);

        addContext(xbrl, "deductions", null);
        addUnit(xbrl, "u1");
        addDeductions(xbrl, options);
        save("sample_iitr.xml", xbrlDocument, options);
        validate(xbrlDocument, options);

    }

    public static void addUnit(XbrlDocument.Xbrl xbrl, String unitId) {

        var unit = xbrl.addNewUnit();

        unit.setId(unitId);
        var measuere = unit.addNewMeasure();
        measuere.setQNameValue(QName.valueOf("AUD"));
    }



    record Dimension<U, V>(Class className, ExplicitMemberDocument.ExplicitMember type, V value) {
    }

    public static void addContext(XbrlDocument.Xbrl xbrl, String contextId, Dimension dimension) {
        var context = xbrl.addNewContext();
        context.setId(contextId);

        var entity = context.addNewEntity();
        var identifier = entity.addNewIdentifier();
        identifier.setScheme("http://www.abr.gov.au/abn");
        identifier.setStringValue("identifier");

        var segment = entity.addNewSegment();
        var segNode = segment.getDomNode();

        var memberDoc = org.xbrl.x2006.xbrldi.ExplicitMemberDocument.Factory.newInstance();
        var explicitMember = memberDoc.addNewExplicitMember();


        explicitMember.setDimension(
                new QName("http://www.sbr.gov.au/ato/iitr", "ReportingParty")
        );
        explicitMember.setStringValue("avalue");

        var ownerDoc = segNode.getOwnerDocument();
        var imported = ownerDoc.importNode(explicitMember.getDomNode(), true);
        segNode.appendChild(imported);

//        add scenaro

        var period = context.addNewPeriod();
        period.setStartDate(Calendar.getInstance());
        period.setEndDate(Calendar.getInstance());


    }

    public static void addDeductions(XbrlDocument.Xbrl xbrl, XmlOptions options) {

        var giftdonationDoc = IncomeTaxDeductionGiftDonationAmountDocument.Factory.newInstance();
        var giftdonation = giftdonationDoc.addNewIncomeTaxDeductionGiftDonationAmount();
        giftdonation.setBigDecimalValue(BigDecimal.ONE);
        giftdonation.setContextRef("deductions");
        giftdonation.setUnitRef("u1");
        validate(giftdonation, options);
        addElementToXMl(giftdonationDoc, xbrl);
    }

    public static void addIntermediaryDetails(XbrlDocument.Xbrl xbrl, XmlOptions options) {
        var businessNumberDoc = IdentifiersAustralianBusinessNumberIdentifierDocument.Factory.newInstance();
//        var data =
        var businessNumber = businessNumberDoc.addNewIdentifiersAustralianBusinessNumberIdentifier();
        businessNumber.setStringValue("11111111111");
        businessNumber.setContextRef("intermediary_context");
        validate(businessNumber, options);
        addElementToXMl(businessNumberDoc, xbrl);

    }

    public static void save(String outputFilePath, XbrlDocument xbrlDocument, XmlOptions opts) throws Exception {
        xbrlDocument.save(new File(outputFilePath), opts);
    }


    public static XmlOptions setOptions() {
        XmlOptions opts = new XmlOptions();
        opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        opts.setValidateStrict();
        opts.setSaveNoXmlDecl();

        return opts;
    }

    public static void addElementToXMl(XmlObject element, XbrlDocument.Xbrl xbrl) {
        try (XmlCursor target = xbrl.newCursor(); XmlCursor source = element.newCursor()) {
            target.toEndToken();  // move just before </xbrl>
            source.toFirstChild(); // move to actual payload element
            source.copyXml(target); // copy safely
        }
    }

    public static void addElementToXMl2(XmlObject element, XbrlDocument.Xbrl xbrl) {
        try (XmlCursor rootCursor = xbrl.newCursor();
             XmlCursor famCursor = element.newCursor()) {

            famCursor.toFirstChild();

            rootCursor.toEndToken(); // move inside <xbrl>
            famCursor.copyXml(rootCursor); // copy element subtree
        }
    }

    public static void validate(XmlObject xbrl, XmlOptions options) {

        // Validation error listener
        List<XmlError> errors = new ArrayList<>();
        options.setErrorListener(errors);

        if (!xbrl.validate(options)) {
            System.out.println("❌ Validation failed for XBRL root:");
            for (XmlError err : errors) {
                System.out.println("  - " + err);
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

    public static XbrlDocument createXbrlDocument() {
        var xbrl = XbrlDocument.Factory.newInstance();

        return xbrl;
    }
}
