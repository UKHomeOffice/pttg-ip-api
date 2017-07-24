package uk.gov.digital.ho.proving.income.api.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.api.FinancialCheckResult
import uk.gov.digital.ho.proving.income.api.FinancialCheckValues
import uk.gov.digital.ho.proving.income.api.IncomeValidator
import uk.gov.digital.ho.proving.income.domain.hmrc.Employer
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments
import uk.gov.digital.ho.proving.income.domain.hmrc.Income

import java.time.LocalDate
import java.time.Month

import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.*

class WeeklyIncomeValidator2Spec extends Specification {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyIncomeValidator2Spec.class);

    int days = 182

    def "valid category A individual is accepted"() {

        given:
        List<Income> incomes = getIncomesAboveThreshold2()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried2(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)

    }


    def "valid category A individual is accepted with exactly 26 records"() {

        given:
        List<Income> incomes = getIncomesExactly26AboveThreshold2()

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried2(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_SALARIED_PASSED)

    }

    def "invalid category A individual is rejected with exactly 26 records as raisedLocalDate is before last payday"() {

        given:
        List<Income> incomes = getIncomesExactly26AboveThreshold2()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 10)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried2(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NOT_ENOUGH_RECORDS)

    }


    def "invalid category A not enough weekly data"() {

        given:
        List<Income> incomes = getIncomesNotEnoughWeeks2()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried2(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NOT_ENOUGH_RECORDS)

    }

    def "invalid category A some weeks below threshold"() {

        given:
        List<Income> incomes = getIncomesSomeBelowThreshold2()
        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAWeeklySalaried2(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.WEEKLY_VALUE_BELOW_THRESHOLD)

    }


    List<Employments> getEmployers(List<Income> incomes) {
        Set<String> employeRef = new HashSet<>()
        for (Income income : incomes) {
            employeRef.add(income.getEmployerPayeReference())
        }
        return employeRef.stream().map({ref -> new Employments(new Employer(employerRefToName(ref), ref))}).collect();
    }

    String employerRefToName(ref) {
        if (ref == PIZZA_HUT_PAYE_REF) {
            return PIZZA_HUT
        }
        if (ref == BURGER_KING_PAYE_REF) {
            return BURGER_KING
        }

        ref
    }

}
