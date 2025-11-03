package com.seotrove.demo.v4_current.dtos;

import au.gov.sbr.ato.iitr.PersonUnstructuredNameFullNameTextDocument;
import com.seotrove.demo.v4_current.annotations.v2.XbrlContextIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.XbrlElementIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.domain.XbrlContextFieldNames;

import java.util.Calendar;

public record TaxData(


        @XbrlContextIdentifier(label = "INT.TrueAndCorrect", field = XbrlContextFieldNames.IDENTIFIER)
        String abn,

//        we do it this way
        @XbrlElementIdentifier(target = PersonUnstructuredNameFullNameTextDocument.class, contextRef = "INT.TrueAndCorrect")
        Integer personName,

        @XbrlContextIdentifier(label = "INT.TrueAndCorrect", field = XbrlContextFieldNames.START_DATE)
        Calendar startDate,

        @XbrlContextIdentifier(label = "INT.TrueAndCorrect", field = XbrlContextFieldNames.END_DATE)
        Calendar endDate,
        Double amount
) {
}