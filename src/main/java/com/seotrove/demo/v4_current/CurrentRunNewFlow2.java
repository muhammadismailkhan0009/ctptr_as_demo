package com.seotrove.demo.v4_current;

import com.seotrove.demo.v4_current.dtos.AdjustmentsGovtSuperContributions;
import com.seotrove.demo.v4_current.dtos.TaxData;
import com.seotrove.demo.v4_current.mapper.context_from_excel.XbrlElementMapper;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x1999.xlink.TypeAttribute;
import org.xbrl.x2003.instance.XbrlDocument;

import javax.xml.namespace.QName;
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

        var mapper = new XbrlElementMapper();
        TaxData dto = new TaxData(
                "7",
                12,
                Calendar.getInstance(),
                Calendar.getInstance(),
                Double.MIN_VALUE
        );
        var dto2 = new AdjustmentsGovtSuperContributions(11.0, "112233");
        dto2.setStartDate(Calendar.getInstance());
        dto2.setEndDate(Calendar.getInstance());

        List<Object> dtos = List.of(dto, dto2);
        mapper.map(dtos, xbrl);

        var unit = xbrl.addNewUnit();
        unit.setId("u1");
        var measure = unit.addNewMeasure();
        measure.setQNameValue(new QName("http://www.xbrl.org/2003/iso4217", "AUD"));

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
