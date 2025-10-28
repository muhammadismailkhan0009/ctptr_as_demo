package com.seotrove.demo.v4_current.mapper.context_from_excel;

import org.w3c.dom.Node;
import org.xbrl.x2003.instance.ContextDocument;
import org.xbrl.x2003.instance.DateUnion;
import org.xbrl.x2003.instance.XbrlDocument;
import org.xbrl.x2006.xbrldi.ExplicitMemberDocument;
import org.xbrl.x2006.xbrldi.TypedMemberDocument;

import javax.xml.namespace.QName;
import java.util.Calendar;

public final class GenericContextBuilder {

    public record ContextData(String label, String identifierValue, Calendar startDate, Calendar endDate) {
    }

    public static ContextDocument.Context build(XbrlDocument.Xbrl xbrl, ContextRow row, ContextData contextData) {
        var ctx = xbrl.addNewContext();
//        ctx.setId(row.label != null ? row.label : "CTX_" + row.seqNum);
        ctx.setId(contextData.label);

        // ENTITY
        var entity = ctx.addNewEntity();
        var identifier = entity.addNewIdentifier();
        identifier.setScheme(row.identifierScheme);
        identifier.setStringValue(contextData.identifierValue);

        // SEGMENT (Dimensions)
        var segment = entity.addNewSegment();
        for (var dim : row.dimensions) {
            if ("explicit".equalsIgnoreCase(dim.type)) {
                addExplicitMember(segment.getDomNode(),
                        "http://www.sbr.gov.au/ato/iitr", dim.name, dim.value);
            } else if ("typed".equalsIgnoreCase(dim.type)) {
                addTypedMember(segment.getDomNode(), "http://www.sbr.gov.au/ato/iitr", dim.name, dim.value);
            }
        }

        // PERIOD
        var period = ctx.addNewPeriod();
        if ("duration".equalsIgnoreCase(row.periodType)) {
            period.setStartDate(contextData.startDate);
            period.setEndDate(contextData.endDate);
        } else {
            period.xsetInstant(DateUnion.Factory.newValue(row.startDate));
        }

        return ctx;
    }

    public static void addExplicitMember(Node segmentNode, String ns, String dim, String val) {
        var memberDoc = ExplicitMemberDocument.Factory.newInstance();
        var member = memberDoc.addNewExplicitMember();
        member.setDimension(new QName(ns, dim));
        member.setQNameValue(new QName(ns, val));
        segmentNode.appendChild(segmentNode.getOwnerDocument().importNode(member.getDomNode(), true));
    }

    public static void addTypedMember(Node segmentNode, String ns, String dim, String val) {
        var memberDoc = TypedMemberDocument.Factory.newInstance();
        var member = memberDoc.addNewTypedMember();
        member.setDimension(new QName(ns, dim));
        segmentNode.appendChild(segmentNode.getOwnerDocument().importNode(member.getDomNode(), true));
    }
}
