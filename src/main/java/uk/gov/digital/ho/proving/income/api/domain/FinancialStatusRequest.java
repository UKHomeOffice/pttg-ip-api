package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
public class FinancialStatusRequest {
    @JsonProperty("individuals")
    private final List<Applicant> applicants;
    @JsonProperty("applicationRaisedDate")
    private final LocalDate applicationRaisedDate;
    @JsonProperty("dependants")
    private final Integer dependants;

    @ConstructorProperties(value={"applicants", "applicationRaisedDate", "dependants"})
    public FinancialStatusRequest(
        List<Applicant> applicants,
        LocalDate applicationRaisedDate,
        Integer dependants
    ) {
        this.applicants = applicants;
        this.applicationRaisedDate = applicationRaisedDate;
        this.dependants = dependants==null?0:dependants;
    }
}


