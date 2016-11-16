package uk.gov.digital.ho.proving.income.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// TODO rename this class
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeProvingResponse {
    private Individual individual;
    private List<Income> incomes;
    private String total;
    private String payFreq;

    public IncomeProvingResponse() {
    }

    public IncomeProvingResponse(Individual individual, List<Income> incomes, String total, String payFreq) {
        this.individual = individual;
        this.incomes = incomes;

        this.total = total;
        this.payFreq = payFreq;
    }

    public Individual getindividual() {
        return individual;
    }

    public void setindividual(Individual individual) {
        this.individual = individual;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPayFreq() {
        return payFreq;
    }

    public void setPayFreq(String payFreq) {
        this.payFreq = payFreq;
    }

    @Override
    public String toString() {
        return "IncomeProvingResponse{" +
                "individual=" + individual +
                ", incomes=" + incomes +
                ", total='" + total + '\'' +
                ", payFreq='" + payFreq + '\'' +
                '}';
    }
}
