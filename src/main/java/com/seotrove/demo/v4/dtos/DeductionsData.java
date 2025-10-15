package com.seotrove.demo.v4.dtos;

import au.gov.sbr.ato.iitr.IncomeTaxDeductionCarAmountDocument;
import com.seotrove.demo.v4.annotations.XbrlElementField;
import com.seotrove.demo.v4.contexts.DeductionsContext;

public record DeductionsData(
        @XbrlElementField(target = IncomeTaxDeductionCarAmountDocument.class,context = DeductionsContext.class)
        Integer amount
) {
}
