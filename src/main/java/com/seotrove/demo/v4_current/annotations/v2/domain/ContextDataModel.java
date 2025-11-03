package com.seotrove.demo.v4_current.annotations.v2.domain;

import java.util.Calendar;

public class ContextDataModel {

    private String label;
    private String identifier;
    private Calendar startDate;
    private Calendar endDate;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void addData(String label, XbrlContextFieldNames field, Object value) {
        var contextData = this;
        contextData.setLabel(label);
        if (XbrlContextFieldNames.IDENTIFIER.equals(field)) {
            contextData.setIdentifier((String) value);
        } else if (XbrlContextFieldNames.START_DATE.equals(field)) {
            contextData.setStartDate((Calendar) value);
        } else if (XbrlContextFieldNames.END_DATE.equals(field)) {
            contextData.setEndDate((Calendar) value);
        }
    }
}
