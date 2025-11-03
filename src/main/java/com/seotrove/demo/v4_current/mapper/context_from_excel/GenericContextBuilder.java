package com.seotrove.demo.v4_current.mapper.context_from_excel;

import com.seotrove.demo.v4_current.annotations.v2.domain.ContextDataModel;
import org.w3c.dom.Node;
import org.xbrl.x2003.instance.DateUnion;
import org.xbrl.x2003.instance.XbrlDocument;
import org.xbrl.x2006.xbrldi.ExplicitMemberDocument;
import org.xbrl.x2006.xbrldi.TypedMemberDocument;

import javax.xml.namespace.QName;

public final class GenericContextBuilder {

    public static void build(XbrlDocument.Xbrl xbrl, ContextRow row, ContextDataModel contextData) {
        var ctx = xbrl.addNewContext();
//        ctx.setId(row.label != null ? row.label : "CTX_" + row.seqNum);
        ctx.setId(contextData.getLabel());

        // ENTITY
        var entity = ctx.addNewEntity();
        var identifier = entity.addNewIdentifier();
        identifier.setScheme(row.identifierScheme);
        identifier.setStringValue(contextData.getLabel());

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
            period.setStartDate(contextData.getStartDate());
            period.setEndDate(contextData.getEndDate());
        } else {
            period.xsetInstant(DateUnion.Factory.newValue(row.startDate));
        }

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
