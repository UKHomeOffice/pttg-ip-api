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

import static MockDataUtils.*
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.BURGER_KING
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.BURGER_KING_PAYE_REF
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.PIZZA_HUT
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.PIZZA_HUT_PAYE_REF

class MonthlyIncomeValidatorSpec extends Specification {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonthlyIncomeValidatorSpec.class);

    int days = 182

    def "valid category A individual is accepted"() {

        given:
        List<Income> incomes = getConsecutiveIncomes2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)

    }

    def "invalid category A individual is rejected (non consecutive)"() {

        given:
        List<Income> incomes = getNoneConsecutiveIncomes2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NON_CONSECUTIVE_MONTHS)

    }

    def "invalid category A individual is rejected (not enough records)"() {

        given:
        List<Income> incomes = getNotEnoughConsecutiveIncomes2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.NOT_ENOUGH_RECORDS)

    }

    def "invalid category A individual is rejected (consecutive but not same employer)"() {

        given:
        List<Income> incomes = getConsecutiveIncomesButDifferentEmployers2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.MULTIPLE_EMPLOYERS)
        categoryAIndividual.getEmployers().contains(BURGER_KING)
        categoryAIndividual.getEmployers().contains(PIZZA_HUT)

    }

    def "invalid category A individual is rejected (consecutive but not enough earnings)"() {

        given:
        List<Income> incomes = getConsecutiveIncomesButLowAmounts2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_VALUE_BELOW_THRESHOLD)

    }

    def "valid category A individual is accepted with different monthly payLocalDates"() {

        given:
        List<Income> incomes = getConsecutiveIncomesWithDifferentMonthlyPayDay2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)

    }

    def "valid category A individual is accepted with exactly the threshold values"() {

        given:
        List<Income> incomes = getConsecutiveIncomesWithExactlyTheAmount2()
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23)
        LocalDate pastDate = subtractDaysFromDate(raisedDate, days)

        when:
        FinancialCheckResult categoryAIndividual = IncomeValidator.validateCategoryAMonthlySalaried(incomes, pastDate, raisedDate, 0, getEmployers(incomes))

        then:
        categoryAIndividual.getFinancialCheckValue().equals(FinancialCheckValues.MONTHLY_SALARIED_PASSED)

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
