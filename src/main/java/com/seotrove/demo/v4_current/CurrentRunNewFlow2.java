package com.seotrove.demo.v4_current;

import com.seotrove.demo.v4_current.dtos.TaxData;
import com.seotrove.demo.v4_current.mapper.context_from_excel.ExcelFileReader;
import com.seotrove.demo.v4_current.mapper.context_from_excel.GenericContextBuilder;
import com.seotrove.demo.v4_current.mapper.context_from_excel.XbrlElementMapper;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x1999.xlink.TypeAttribute;
import org.xbrl.x2003.instance.XbrlDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.seotrove.demo.v4_current.XmlBeansRun.setOptions;

public class CurrentRunNewFlow2 {

    public static void main(String[] args) throws IOException {
        var options = setOptions();
        var xbrlDoc = XbrlDocument.Factory.newInstance();
        var xbrl = createXbrl(xbrlDoc);

        var contextData = new GenericContextBuilder.ContextData("INT.TrueAndCorrect", "112233",
                Calendar.getInstance(),
                Calendar.getInstance());
        var contextRow = ExcelFileReader.readContextFromExcel(new File("src/main/resources/iitr.xlsx"),
                contextData.label());
        GenericContextBuilder.build(xbrl, contextRow, contextData);

        TaxData dto = new TaxData(
                "7",
                12
        );
        XbrlElementMapper.map(dto, xbrl);

//        var taxClientinfo = new TaxAgentInfo("abc", "17801003");
//        XbrlMapper.map(taxClientinfo, xbrl);

//        var deductionsContext = new DeductionsContextData("220115555");
//        XbrlMapper.map(deductionsContext, xbrl);

        xbrlDoc.save(new File("iitr_intermediary.xml"), options);
        validate(xbrlDoc, options);

    }

    public static XbrlDocument.Xbrl createXbrl(XbrlDocument xbrlDoc) {
        var xbrl = xbrlDoc.addNewXbrl();
        var schemaRef = xbrl.addNewSchemaRef();
        schemaRef.setType(TypeAttribute.Type.Enum.forString("simple"));
        schemaRef.setHref("http://sbr.gov.au/taxonomy/sbr_au_reports/ato/iitr/iitr_0012/ato.iitr.0012.2025.lodge.01.00.report.xsd");

        return xbrl;
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

}
