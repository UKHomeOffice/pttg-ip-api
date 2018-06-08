package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Applicant {
    private String forename;
    private String surname;
    private LocalDate dateOfBirth;
    private String nino;

    @JsonCreator
    public Applicant(@JsonProperty("forename") String forename, @JsonProperty("surname") String surname, @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("nino") String nino ) {
        this.forename = forename;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.nino = nino;

    }

    @Override
    public String toString() {
        return "Applicant{" +
            "forename='" + forename + '\'' +
            ", surname='" + surname + '\'' +
            ", dateOfBirth=" + dateOfBirth +
            ", nino='" + nino + '\'' +
            '}';
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

    public String getNino() {
        return nino;
    }
}
