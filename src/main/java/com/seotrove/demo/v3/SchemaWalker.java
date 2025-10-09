package com.seotrove.demo.v3;

import org.eclipse.xsd.*;

public class SchemaWalker {

    public static void printSchema(XSDSchema schema) {
        System.out.println("Schema targetNamespace = " + schema.getTargetNamespace());
        for (XSDElementDeclaration element : schema.getElementDeclarations()) {
            System.out.println("trying out");
            printElement(element, "  ");
        }
    }

    private static void printElement(XSDElementDeclaration decl, String indent) {
        String qName = decl.getQName();
        System.out.println(indent + "Element: " + qName
                + " (abstract=" + decl.isAbstract() + ")");

        if (decl.getSubstitutionGroupAffiliation() != null) {
            System.out.println(indent + "  ↳ substitutionGroup: "
                    + decl.getSubstitutionGroupAffiliation().getQName());
        }

        XSDTypeDefinition type = decl.getTypeDefinition();
        if (type != null) {
            System.out.println(indent + "  ↳ type: " + type.getQName());
            walkType(type, indent + "    ");
        }
    }

    private static void walkType(XSDTypeDefinition type, String indent) {
        if (type instanceof XSDSimpleTypeDefinition simple) {
            System.out.println(indent + "(simpleType base = "
                    + (simple.getBaseTypeDefinition() != null
                    ? simple.getBaseTypeDefinition().getName()
                    : "anySimpleType") + ")");
        } else if (type instanceof XSDComplexTypeDefinition complex) {

            // Print attributes
            for (XSDAttributeUse attrUse : complex.getAttributeUses()) {
                XSDAttributeDeclaration attrDecl = attrUse.getAttributeDeclaration();
                System.out.println(indent + "Attribute: "
                        + attrDecl.getQName()
                        + " (type=" + attrDecl.getTypeDefinition().getName()
                        + ", use=" + attrUse.getUse() + ")");
            }

            // Handle content model
            XSDParticle particle = complex.getComplexType();
            if (particle != null) {
                walkParticleNew(particle, indent);
            }
        }
    }


    public static void walkSchema(XSDSchema schema) {
        for (XSDElementDeclaration decl : schema.getElementDeclarations()) {
            System.out.println("Global element: " + decl.getQName() +
                    " (abstract=" + decl.isAbstract() + ")");
            walkElement(decl, 1);
        }
    }

    private static void walkElement(XSDElementDeclaration decl, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "- " + decl.getQName());

        if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition complex) {
            if (complex.getContent() instanceof XSDParticle particle) {
                walkParticle(particle, depth + 1);
            }
        }

        // Handle substitution groups
        for (XSDElementDeclaration candidate : decl.getSchema().getElementDeclarations()) {
            if (candidate.getSubstitutionGroupAffiliation() == decl) {
                System.out.println(indent + "  ↳ Substitutable by: " + candidate.getQName());
            }
        }
    }

    private static void walkParticle(XSDParticle particle, int depth) {
        XSDTerm term = particle.getTerm();
        if (term instanceof XSDElementDeclaration child) {
            walkElement(child, depth);
        } else if (term instanceof XSDModelGroup group) {
            for (XSDParticle p : group.getContents()) {
                walkParticle(p, depth);
            }
        }
    }

    private static void walkParticleNew(XSDParticle particle, String indent) {
        XSDTerm term = particle.getTerm();
        if (term instanceof XSDElementDeclaration) {
            XSDElementDeclaration child = (XSDElementDeclaration) term;
            System.out.println(indent + "Child element: " + child.getQName()
                    + " (alias path=" + child.getAliasName()
                    + " (minOccurs=" + particle.getMinOccurs()
                    + ", maxOccurs=" + particle.getMaxOccurs() + ")");
            printElement(child, indent + "  ");
        } else if (term instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) term;
            System.out.println(indent + "ModelGroup (" + group.getCompositor() + ")");
            for (XSDParticle childParticle : group.getParticles()) {
                walkParticleNew(childParticle, indent + "  ");
            }
        }
    }

}

