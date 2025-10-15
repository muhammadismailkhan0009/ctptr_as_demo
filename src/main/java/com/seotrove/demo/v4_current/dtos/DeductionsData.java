package com.seotrove.demo.v4_current.dtos;

import au.gov.sbr.ato.iitr.IncomeTaxDeductionCarAmountDocument;
import com.seotrove.demo.v4_current.annotations.XbrlElementField;
import com.seotrove.demo.v4_current.contexts.DeductionsContext;

public record DeductionsData(
        @XbrlElementField(target = IncomeTaxDeductionCarAmountDocument.class,context = DeductionsContext.class)
        Integer amount
) {
}
