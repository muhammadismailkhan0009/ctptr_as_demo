package com.seotrove.demo.v4_current.annotations.v2;

import com.seotrove.demo.v4_current.annotations.v2.domain.XbrlContextFieldNames;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XbrlContextIdentifier {

    String label();

    /**
     * The specific context field type to fill dynamically.
     * Each context class should expose its own nested types for valid fields
     * (e.g., ContextEntityType.Identifier, ContextPeriodType.StartDate, etc.).
     */
    XbrlContextFieldNames field();

}
