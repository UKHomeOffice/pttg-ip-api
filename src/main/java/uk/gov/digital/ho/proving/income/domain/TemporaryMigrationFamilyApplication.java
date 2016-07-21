package uk.gov.digital.ho.proving.income.domain;

import java.time.LocalDate;

public class TemporaryMigrationFamilyApplication implements Application {
    private Individual individual;
    //    @JsonFormat(pattern="dd MMM yyyy")
    private LocalDate applicationRaisedDate;

    private String category;

    public TemporaryMigrationFamilyApplication() {
    }

    public TemporaryMigrationFamilyApplication(Individual individual, LocalDate applicationRaisedDate, String category, boolean meetsFinancialRequirements) {
        this.individual = individual;
        this.applicationRaisedDate = applicationRaisedDate;
        this.category = category;
    }

    public Individual getIndividual() {
        return individual;
    }

}
