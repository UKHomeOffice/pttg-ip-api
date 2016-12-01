package uk.gov.digital.ho.proving.income.api.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.api.FinancialCheckResult
import uk.gov.digital.ho.proving.income.api.FinancialCheckValues
import uk.gov.digital.ho.proving.income.api.IncomeValidator
import uk.gov.digital.ho.proving.income.domain.Income
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse
import uk.gov.digital.ho.proving.income.domain.Individual

import java.time.LocalDate
import java.time.Month

import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.*

class WeeklyIncomeValidatorSpec extends Specification {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyIncomeValidatorSpec.class);

    int days = 182

    def "valid category A individual is accepted"() {

        given:
        List<Income> incomes = getIncomesAboveThreshold()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)

    }


    def "valid category A individual is accepted with exactly 26 records"() {

        given:
        List<Income> incomes = getIncomesExactly26AboveThreshold()

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)

    }

    def "invalid category A individual is rejected with exactly 26 records as raisedLocalDate is before last payday"() {

        given:
        List<Income> incomes = getIncomesExactly26AboveThreshold()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 10)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NOT_ENOUGH_RECORDS)

    }


    def "invalid category A not enough weekly data"() {

        given:
        List<Income> incomes = getIncomesNotEnoughWeeks()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NOT_ENOUGH_RECORDS)

    }

    def "invalid category A some weeks below threshold"() {

        given:
        List<Income> incomes = getIncomesSomeBelowThreshold()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_VALUE_BELOW_THRESHOLD)

    }

    List<String> getEmployers(List<Income> incomes) {
        Map<String, String> employers = new HashMap<>()
        for (Income income : incomes) {
            employers.put(income.getEmployer(), income.getEmployer())
        }
        return new ArrayList(employers.values())
    }

}
