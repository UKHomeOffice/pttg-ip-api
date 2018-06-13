package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Getter
@Accessors(fluent = true)
@ToString
public class FinancialStatusRequest {
    private final List<Applicant> applicants;
    private final LocalDate applicationRaisedDate;
    private final Integer dependants;

    @JsonCreator
    public FinancialStatusRequest(@JsonProperty("individuals") List<Applicant> applicants, @JsonProperty("applicationRaisedDate") LocalDate applicationRaisedDate, @JsonProperty("dependants") Integer dependants) {
        this.applicants = applicants;
        this.applicationRaisedDate = applicationRaisedDate;
        this.dependants = dependants==null?0:dependants;
    }
}


