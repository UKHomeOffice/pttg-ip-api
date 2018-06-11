package uk.gov.digital.ho.proving.income.api;

import java.util.List;

public class CheckedIndividual {

    private String nino;
    private List<String> employers;

    public CheckedIndividual(String nino, List<String> employers) {
        this.nino = nino;
        this.employers = employers;
    }

    public String getNino() {
        return nino;
    }

    public List<String> getEmployers() {
        return employers;
    }
}
