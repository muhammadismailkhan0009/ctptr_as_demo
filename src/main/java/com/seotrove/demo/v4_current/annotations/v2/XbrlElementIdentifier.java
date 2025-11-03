package com.seotrove.demo.v4_current.annotations.v2;

import com.seotrove.demo.v4_current.annotations.v2.domain.units.UnitType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XbrlElementIdentifier {

    Class<?> target();

    /**
     * The context builder class this element belongs to.
     * Example:
     * ReportingPartyContext.class
     */
    String contextRef();

    String unitRef() default "";

    UnitType unitType() default UnitType.NONE;
}
