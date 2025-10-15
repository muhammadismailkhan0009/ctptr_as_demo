package com.seotrove.demo.v4_current.annotations;

import java.lang.annotation.*;

/**
 * Links a DTO field to a specific XMLBeans-generated XBRL fact element.
 * The target refers to the generated class that represents the element node
 * (e.g. IncomeSalaryOrWagesAmountDocument.IncomeSalaryOrWagesAmount.class).
 * <p>
 * The context and unit references ensure proper attribute linking.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XbrlElementField {

    /**
     * The XMLBeans-generated element class representing the fact.
     * Example:
     * IncomeSalaryOrWagesAmountDocument.IncomeSalaryOrWagesAmount.class
     */
    Class<?> target();

    /**
     * The context builder class this element belongs to.
     * Example:
     * ReportingPartyContext.class
     */
    Class<?> context();

    /**
     * The unit class if applicable (for numeric facts).
     * Optional: leave default if not used.
     * Example:
     * AudUnit.class
     */
    Class<?> unit() default Void.class;

    /**
     * Decimals attribute for numeric facts (optional).
     */
    int decimals() default -1;

    /**
     * Mark as xsi:nil = true if value should be null.
     */
    boolean nil() default false;

    /**
     * Ordering hint for deterministic output.
     */
    int order() default 0;
}

