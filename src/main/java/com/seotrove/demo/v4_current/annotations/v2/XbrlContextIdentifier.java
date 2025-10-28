package com.seotrove.demo.v4_current.annotations.v2;

import org.xbrl.x2003.instance.ContextEntityType;

public @interface XbrlContextIdentifier {

    String label();

    /**
     * The specific context field type to fill dynamically.
     * Each context class should expose its own nested types for valid fields
     * (e.g., ContextEntityType.Identifier, ContextPeriodType.StartDate, etc.).
     */
    Class<ContextEntityType.Identifier> field();
}
