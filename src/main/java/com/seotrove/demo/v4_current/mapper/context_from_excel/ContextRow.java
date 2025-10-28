package com.seotrove.demo.v4_current.mapper.context_from_excel;

import java.util.ArrayList;
import java.util.List;

public class ContextRow {
    public int seqNum;
    public String label;
    public String startDate;
    public String endDate;
    public String description;
    public String periodType;
    public int minOccurs;
    public int maxOccurs;
    public String identifierScheme;
    public String identifierValue;

    // Up to 3 dimensions (you can make this dynamic later)
    public List<Dimension> dimensions = new ArrayList<>();

    public String action;
    public String workitem;
    public String status;
    public String updateDate;
    public String updateDescription;
    public String mstVersion;

    public static class Dimension {
        public String nsPrefix;
        public String name;
        public String type;
        public String value;
        public String element;
        public String alias;
    }
}
