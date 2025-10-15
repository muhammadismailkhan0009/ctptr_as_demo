package com.seotrove.demo.v4.dtos;

import com.seotrove.demo.v4.annotations.XbrlContextField;
import com.seotrove.demo.v4.contexts.IntermediaryContext;
import org.xbrl.x2003.instance.ContextEntityType;

public record TaxData(
//            it is context identifier with scheme and value
        @XbrlContextField(target = IntermediaryContext.class, field = ContextEntityType.Identifier.class)
        String abn
) {
}