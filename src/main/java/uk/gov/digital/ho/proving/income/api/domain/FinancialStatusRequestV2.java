package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Deprecated
public class FinancialStatusRequestV2 {
    private final String nino;
    private final String forename;
    private final String surname;
    private final LocalDate dateOfBirth;
    private final LocalDate applicationRaisedDate;
    private final Integer dependants;

    @JsonCreator
    public FinancialStatusRequestV2(@JsonProperty("nino") String nino, @JsonProperty("forename") String forename, @JsonProperty("surname") String surname, @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("applicationRaisedDate") LocalDate applicationRaisedDate, @JsonProperty("dependants") Integer dependants) {
        this.nino = nino;
        this.forename = forename;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.applicationRaisedDate = applicationRaisedDate;
        this.dependants = dependants!=null? dependants : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinancialStatusRequest{");
        sb.append("nino='").append(nino).append('\'');
        sb.append(", forename='").append(forename).append('\'');
        sb.append(", surname='").append(surname).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", applicationRaisedDate=").append(applicationRaisedDate);
        sb.append(", dependants=").append(dependants);
        sb.append('}');
        return sb.toString();
    }

    public String getNino() {
        return nino;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getApplicationRaisedDate() {
        return applicationRaisedDate;
    }

    public Integer getDependants() {
        return dependants;
    }
}
