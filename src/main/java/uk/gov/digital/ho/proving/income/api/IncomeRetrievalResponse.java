package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import uk.gov.digital.ho.proving.income.domain.Income;
import uk.gov.digital.ho.proving.income.domain.Individual;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class IncomeRetrievalResponse extends BaseResponse {

    @JsonInclude(Include.NON_NULL)
    private final Individual individual;

    @JsonInclude(Include.NON_NULL)
    private final List<Income> incomes;

    @JsonInclude(Include.NON_NULL)
    private final String total;

    public IncomeRetrievalResponse(Individual individual, List<Income> incomes) {
        this.individual = individual;
        this.incomes = incomes;
        total = calculateTotal();
    }

    private String calculateTotal() {
        Stream<BigDecimal> decimalValues = incomes.stream().map (income -> new BigDecimal(income.getIncome()));
        BigDecimal total = decimalValues.reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.toString();
    }

    public Individual getIndividual() {
        return individual;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public String getTotal() {
        return total;
    }
}
