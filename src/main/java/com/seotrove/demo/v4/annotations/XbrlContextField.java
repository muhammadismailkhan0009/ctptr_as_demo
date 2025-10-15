package com.seotrove.demo.v4.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XbrlContextField {

    /**
     * The hardcoded context class this field contributes to.
     * Example: IntermediaryContext.class
     */
    Class<?> target();

    /**
     * The specific context field type to fill dynamically.
     * Each context class should expose its own nested types for valid fields
     * (e.g., ContextEntityType.Identifier, ContextPeriodType.StartDate, etc.).
     */
    Class<?> field();
}

/*
@XbrlContextField(taget = IntermediaryContext, field = ContextEntityType.Identifier.class)
private String tfn;

@XbrlContextField(taget = IntermediaryContext, field = ContextPeriodType.StartDate.class)
private Calender startDate;

 */