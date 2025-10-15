package com.seotrove.demo.v4.dtos;

import au.gov.sbr.ato.iitr.IdentifiersTaxAgentClientReferenceTextDocument;
import au.gov.sbr.ato.iitr.IdentifiersTaxAgentNumberIdentifierDocument;
import com.seotrove.demo.v4.annotations.XbrlElementField;
import com.seotrove.demo.v4.contexts.IntermediaryContext;

public record TaxAgentInfo(
        @XbrlElementField(target = IdentifiersTaxAgentClientReferenceTextDocument.class, context = IntermediaryContext.class)
        String taxAgentClientReference,

        @XbrlElementField(target = IdentifiersTaxAgentNumberIdentifierDocument.class, context = IntermediaryContext.class)
        String taxAgentNumber
) {
}
