package com.seotrove.demo.v4_current.dtos;

import au.gov.sbr.ato.iitr.IncomeTaxInvestmentPartnershipOtherSourceAmountDocument;
import com.seotrove.demo.v4_current.annotations.v2.XbrlContextIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.XbrlElementIdentifier;
import com.seotrove.demo.v4_current.annotations.v2.domain.XbrlContextFieldNames;

import java.util.Calendar;

public class AdjustmentsGovtSuperContributions {

    @XbrlElementIdentifier(
            target = IncomeTaxInvestmentPartnershipOtherSourceAmountDocument.class,
            contextRef = "RP",
            unitRef = "u1"
    )
    private double incomeFromDifferentSource;

    @XbrlContextIdentifier(
            label = "RP",
            field = XbrlContextFieldNames.IDENTIFIER
    )
    private String tfn;

    @XbrlContextIdentifier(
            label = "RP",
            field = XbrlContextFieldNames.START_DATE
    )
    private Calendar startDate;

    @XbrlContextIdentifier(
            label = "RP",
            field = XbrlContextFieldNames.END_DATE
    )
    private Calendar endDate;

    public void setIncomeFromDifferentSource(double incomeFromDifferentSource) {
        this.incomeFromDifferentSource = incomeFromDifferentSource;
    }

    public void setTfn(String tfn) {
        this.tfn = tfn;
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

    // Constructor
    public AdjustmentsGovtSuperContributions(double incomeFromDifferentSource, String tfn) {
        this.incomeFromDifferentSource = incomeFromDifferentSource;
        this.tfn = tfn;
    }

    // Getters
    public double getIncomeFromDifferentSource() {
        return incomeFromDifferentSource;
    }

    public String getTfn() {
        return tfn;
    }

}

