package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.hmrc.domain.Income;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonthlyIncomeAggregator {
    public static Map<Integer, BigDecimal> aggregateMonthlyIncome(List<Income> incomes) {
        return incomes.stream()
            .sorted(Comparator.comparing(Income::paymentDate))
            .collect(
                Collectors.toMap(
                    Income::yearAndMonth,
                    Income::payment,
                    BigDecimal::add,
                    LinkedHashMap::new));
    }
}
