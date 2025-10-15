package com.seotrove.demo.v4_current.dtos;

import au.gov.sbr.ato.iitr.IdentifiersTaxAgentClientReferenceTextDocument;
import au.gov.sbr.ato.iitr.IdentifiersTaxAgentNumberIdentifierDocument;
import com.seotrove.demo.v4_current.annotations.XbrlElementField;
import com.seotrove.demo.v4_current.contexts.IntermediaryContext;

public record TaxAgentInfo(
        @XbrlElementField(target = IdentifiersTaxAgentClientReferenceTextDocument.class, context = IntermediaryContext.class)
        String taxAgentClientReference,

        @XbrlElementField(target = IdentifiersTaxAgentNumberIdentifierDocument.class, context = IntermediaryContext.class)
        String taxAgentNumber
) {
}
