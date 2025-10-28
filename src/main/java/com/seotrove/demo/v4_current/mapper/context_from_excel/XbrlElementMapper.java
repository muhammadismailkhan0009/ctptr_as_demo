package com.seotrove.demo.v4_current.mapper.context_from_excel;

import com.seotrove.demo.v4_current.annotations.v2.XbrlElementIdentifier;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
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
public final class XbrlElementMapper {

    /**
     * Builds an entire XBRL instance document from any annotated DTO instance.
     */

    public static void addContext() {
    }

    public static void map(Object dto, XbrlDocument.Xbrl xbrl) {
        Objects.requireNonNull(dto, "DTO instance cannot be null");

        try {
            // Create the root XBRL document

            Map<Class<?>, Object> contextCache = new HashMap<>();

            // === Phase 1: Handle Context Fields ===

            // === Phase 2: Handle Element Fields ===
            for (Field field : dto.getClass().getDeclaredFields()) {
                XbrlElementIdentifier elAnn = field.getAnnotation(XbrlElementIdentifier.class);
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


                    // Get the static field's value (pass null for static fields)
                    Method contextMethod = elementInstance.getClass().getMethod("setContextRef", String.class);
                    contextMethod.invoke(elementInstance, elAnn.context());
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


