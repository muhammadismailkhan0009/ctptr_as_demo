package com.seotrove.demo.v4_current.contexts;

import org.w3c.dom.Node;
import org.xbrl.x2003.instance.ContextDocument;
import org.xbrl.x2003.instance.ContextEntityType;
import org.xbrl.x2003.instance.DateUnion;
import org.xbrl.x2003.instance.impl.ContextDocumentImpl;
import org.xbrl.x2006.xbrldi.ExplicitMemberDocument;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class DeductionsContext {

    public static final String ID = "RP";

    /**
     * Key: XMLBeans (or chosen) field-class you reference in @XbrlContextField(field=…)
     * Val: setter that knows how to write that value into this context.
     *
     * No if/switch at runtime — pure lookup and apply.
     */
    private static final Map<Class<?>, BiConsumer<ContextValue, Object>> SETTERS;

    static {
        Map<Class<?>, BiConsumer<ContextValue, Object>> m = new HashMap<>();

        // Example keys below — use the actual XMLBeans classes you reference in your annotations.
        // If your annotations use your own "marker" classes, just put those here as keys instead.

        // au.gov.sbr.ato.iitr.ContextEntityType.Identifier (or your chosen class)
        m.put(ContextEntityType.Identifier.class,
                (cv, v) -> cv.ctx.getEntity().getIdentifier().setStringValue((String) v));

        SETTERS = Collections.unmodifiableMap(m);
    }

    private DeductionsContext() {}

    /** Builds the static skeleton; dynamic values are applied later via apply(...). */
    public static ContextDocument.Context createEmpty() {
        ContextDocument.Context ctx = ContextDocument.Context.Factory.newInstance();
        ctx.setId(ID);

        // ---- ENTITY ----
        var entity = ctx.addNewEntity();
        var identifier = entity.addNewIdentifier();
        identifier.setScheme("http://www.ato.gov.au/tfn");
        // value set later via setter

        // segment with fixed dimensions
        var segment = entity.addNewSegment();
        addExplicitMember(segment.getDomNode(),
                "http://www.sbr.gov.au/ato/iitr", "ReportPartyTypeDimension", "ReportingParty");
        // ---- PERIOD (duration) ----
        var period = ctx.addNewPeriod();
        // defaults; real dates set later via setter if provided
        period.xsetStartDate(DateUnion.Factory.newValue("2024-07-01"));
        period.xsetEndDate(DateUnion.Factory.newValue("2025-06-30"));

        return ctx;
    }

    /** Apply a single dynamic value by the exact field class referenced in @XbrlContextField(field=…). */
    public static void apply(ContextDocumentImpl.ContextImpl ctx, Class<?> fieldClass, Object value) {
        BiConsumer<ContextValue, Object> setter = SETTERS.get(fieldClass);
        if (setter == null) {
            throw new IllegalArgumentException("No setter registered for field class: " + fieldClass.getName());
        }
        setter.accept(new ContextValue(ctx), value);
    }

    // ---------- helpers ----------

    private static void addExplicitMember(Node segmentNode, String ns, String dim, String val) {
        var memberDoc = ExplicitMemberDocument.Factory.newInstance();
        var member = memberDoc.addNewExplicitMember();
        member.setDimension(new QName(ns, dim, ""));
        member.setQNameValue(new QName(ns, val));
        segmentNode.appendChild(segmentNode.getOwnerDocument().importNode(member.getDomNode(), true));
    }

    /** Tiny wrapper so our BiConsumer signatures stay neat. */
    private record ContextValue(ContextDocument.Context ctx) {}
}

