package com.seotrove.demo.v4_current.annotations.v2;

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
    String context();
}
