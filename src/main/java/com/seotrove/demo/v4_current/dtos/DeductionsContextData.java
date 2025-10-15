package com.seotrove.demo.v4_current.dtos;

import com.seotrove.demo.v4_current.annotations.XbrlContextField;
import com.seotrove.demo.v4_current.contexts.DeductionsContext;
import org.xbrl.x2003.instance.ContextEntityType;

public class DeductionsContextData {

    @XbrlContextField(target = DeductionsContext.class,field = ContextEntityType.Identifier.class)
    private String tfn;

    public DeductionsContextData(String tfn) {
        this.tfn = tfn;
    }
}
