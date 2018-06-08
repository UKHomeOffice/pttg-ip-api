package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;


public class FinancialStatusRequest {
    private final List<Applicant> applicants;
    private final LocalDate applicationRaisedDate;
    private final Integer dependants;

    @JsonCreator
    public FinancialStatusRequest(@JsonProperty("individuals") List<Applicant> applicants, @JsonProperty("applicationRaisedDate") LocalDate applicationRaisedDate, @JsonProperty("dependants") Integer dependants) {
        this.applicants = applicants;
        this.applicationRaisedDate = applicationRaisedDate;
        this.dependants = dependants!=null? dependants : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinancialStatusRequest{");
        String prefix = "";
        for(Applicant applicant : applicants) {
            sb.append(prefix).append("nino='").append(applicant.getNino()).append('\'');
            sb.append(", forename='").append(applicant.getForename()).append('\'');
            sb.append(", surname='").append(applicant.getSurname()).append('\'');
            sb.append(", dateOfBirth=").append(applicant.getDateOfBirth());
            prefix = ", ";
        }
        sb.append(", applicationRaisedDate=").append(applicationRaisedDate);
        sb.append(", dependants=").append(dependants);
        sb.append('}');
        return sb.toString();
    }

    public LocalDate getApplicationRaisedDate() {
        return applicationRaisedDate;
    }

    public Integer getDependants() {
        return dependants;
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }
}


