package com.seotrove.demo.v4_current.dtos;

import au.gov.sbr.ato.iitr.PersonUnstructuredNameFullNameTextDocument;
import com.seotrove.demo.v4_current.annotations.v2.XbrlElementIdentifier;

public record TaxData(
//            it is context identifier with scheme and value. ignore this for now cuz it is being set in context data
        String abn,

//        we do it this way
        @XbrlElementIdentifier(target = PersonUnstructuredNameFullNameTextDocument.class, context = "INT.TrueAndCorrect")
        Integer personName
) {
}