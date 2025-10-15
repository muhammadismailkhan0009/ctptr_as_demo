package com.seotrove.demo.v4.mapper;


import com.seotrove.demo.v4.annotations.XbrlContextField;
import com.seotrove.demo.v4.annotations.XbrlElementField;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xbrl.x2003.instance.ContextDocument;
import org.xbrl.x2003.instance.XbrlDocument;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Core runtime mapper that builds an XBRL document by:
 * 1. Building contexts (via @XbrlContextField)
 * 2. Building elements (via @XbrlElementField)
 * <p>
 * All class and field linkages are resolved via reflection and annotation metadata.
 */
public final class XbrlMapper {

    private XbrlMapper() {
    }

    /**
     * Builds an entire XBRL instance document from any annotated DTO instance.
     */

    public static void addContext(){}
    public static void map(Object dto, XbrlDocument.Xbrl xbrl) {
        Objects.requireNonNull(dto, "DTO instance cannot be null");

        try {
            // Create the root XBRL document

            Map<Class<?>, Object> contextCache = new HashMap<>();

            // === Phase 1: Handle Context Fields ===
            for (Field field : dto.getClass().getDeclaredFields()) {
                XbrlContextField ctxAnn = field.getAnnotation(XbrlContextField.class);
                if (ctxAnn == null) continue;

                field.setAccessible(true);
                Object value = field.get(dto);
                if (value == null) continue;

                // Create or reuse cached context instance
                var contextClass = ctxAnn.target();
                var existing = contextCache.get(contextClass);
                if (existing == null) {
                    // call createEmpty() reflectively
                    var createEmpty = contextClass.getDeclaredMethod("createEmpty");
                    Object contextInstance = createEmpty.invoke(null);
                    contextCache.put(contextClass, contextInstance);
                    existing = contextInstance;
                }

                // Apply dynamic value using IntermediaryContext.apply()
                var applyMethod = contextClass.getDeclaredMethod("apply",
                        existing.getClass(), Class.class, Object.class);

                // The 2nd argument is the XMLBeans field class (e.g., ContextEntityType.Identifier.class)
                applyMethod.invoke(null, existing, ctxAnn.field(), value);
            }

            // === Add built contexts to the XBRL document ===
            for (Object ctxObj : contextCache.values()) {
                var ctxOb = (ContextDocument.Context) ctxObj;
                // Each context object is an XMLBeans ContextDocument.Context
                xbrl.addNewContext().set(ctxOb);
            }

            // === Phase 2: Handle Element Fields ===
            for (Field field : dto.getClass().getDeclaredFields()) {
                XbrlElementField elAnn = field.getAnnotation(XbrlElementField.class);
                if (elAnn == null) continue;

                field.setAccessible(true);
                Object value = field.get(dto);
                if (value == null) continue;

                Class<?> docClass = elAnn.target();
                System.out.println("🔧 Processing: " + field.getName() + " → " + docClass.getSimpleName());

                // ✅ Use reflection to access static Factory field
                Field factoryField = docClass.getField("Factory");
                Object factoryInstance = factoryField.get(null); // static field

                // ✅ Call factory.newInstance()
                Method newInstanceMethod = factoryInstance.getClass().getMethod("newInstance");
                Object docInstance = newInstanceMethod.invoke(factoryInstance);

                // ✅ Call addNew...() method to get inner element
                String addNewMethodName = "addNew" + docClass.getSimpleName().replace("Document", "");
                Method addNewMethod = docClass.getMethod(addNewMethodName);
                Object elementInstance = addNewMethod.invoke(docInstance);

                // ✅ Set value

                try {
                    Method m = elementInstance.getClass().getMethod("setStringValue", String.class);
                    m.invoke(elementInstance, value.toString());
                } catch (NoSuchMethodException e) {
                    System.err.println("❌ No suitable setter for: " + elementInstance.getClass().getName());
                }

                // ✅ Optional: set contextRef
                try {
                    // elAnn.context() returns the Class<?> of your context
                    Class<?> contextClass = elAnn.context();

                    // Try to get the "ID" field
                    Field idField = contextClass.getField("ID");

                    // Get the static field's value (pass null for static fields)
                    String contextId = (String) idField.get(null);
                    Method contextMethod = elementInstance.getClass().getMethod("setContextRef", String.class);
                    contextMethod.invoke(elementInstance, contextId);
                } catch (NoSuchMethodException ignored) {
                }
                addElementToXMl((XmlObject) docInstance, xbrl);

            }

        } catch (Exception e) {
            throw new RuntimeException("Error mapping XBRL DTO", e);
        }
    }

    public static void addElementToXMl(XmlObject element, XbrlDocument.Xbrl xbrl) {
        try (XmlCursor target = xbrl.newCursor(); XmlCursor source = element.newCursor()) {
            target.toEndToken();  // move just before </xbrl>
            source.toFirstChild(); // move to actual payload element
            source.copyXml(target); // copy safely
        }
    }

    public static XmlOptions setOptions() {
        XmlOptions opts = new XmlOptions();
        opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        opts.setValidateStrict();

        return opts;
    }
}

