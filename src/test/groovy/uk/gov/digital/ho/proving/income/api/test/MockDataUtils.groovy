package uk.gov.digital.ho.proving.income.api.test

import uk.gov.digital.ho.proving.income.domain.Individual
import uk.gov.digital.ho.proving.income.domain.hmrc.Employer
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments
import uk.gov.digital.ho.proving.income.domain.hmrc.Income

import java.time.LocalDate
import java.time.Month

class MockDataUtils {

    final static String PIZZA_HUT = "Pizza Hut"
    final static String BURGER_KING = "Burger King"
    final static String PIZZA_HUT_PAYE_REF = "Pizza Hut/ref"
    final static String BURGER_KING_PAYE_REF = "Burger King/ref"

    final static String weeklyThreshold = "357.69"
    final static String aboveThreshold = "357.70"
    final static String belowThreshold = "357.68"

    static def getIndividual() {
        Individual individual = new Individual()
        individual.title = "Mr"
        individual.forename = "Duncan"
        individual.surname = "Sinclair"
        individual.nino = "AA123456A"
        individual
    }

    static def getConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1400"), getDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF ))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.FEBRUARY, 15), 1, null, BURGER_KING_PAYE_REF ))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MARCH, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getNoneConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.FEBRUARY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 16), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getNotEnoughConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1600"),getDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"),getDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"),getDate(2015, Month.APRIL, 15),1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"),getDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"),getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getConsecutiveIncomesButDifferentEmployers2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 15), 1, null, BURGER_KING_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }


    static def getConsecutiveIncomesButLowAmounts2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JANUARY, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1400"), getDate(2015, Month.JULY, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1400"), getDate(2015, Month.AUGUST, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getConsecutiveIncomesWithDifferentMonthlyPayDay2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1400"), getDate(2015, Month.JANUARY, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 16),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 17),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.JULY, 14),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.FEBRUARY, 15),  1, null, BURGER_KING_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.AUGUST, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15),  1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }


    static def getConsecutiveIncomesWithExactlyTheAmount2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.MAY, 16), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.JUNE, 17), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.JULY, 14), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.FEBRUARY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal("1550"), getDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }


    static def getIncomesAboveThreshold2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 11), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 4), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 30), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 23), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 16), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 9), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 2), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 26), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 19), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 12), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 5), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 31), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 27), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 20), null, 1, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getIncomesExactly26AboveThreshold2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 11), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 4), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 30), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 23), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 16), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 9), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 2), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 26), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 19), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 12), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 5), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 31), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 17), null, 1, PIZZA_HUT_PAYE_REF))

        incomes
    }

    static def getIncomesNotEnoughWeeks2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 11), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 4), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 30), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 23), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 16), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 9), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 31), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 27), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 20), null, 1, PIZZA_HUT_PAYE_REF))

        incomes
    }

    static def getIncomesSomeBelowThreshold2() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 11), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.AUGUST, 4), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JULY, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 30), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(belowThreshold), getDate(2015, Month.JUNE, 23), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 16), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 9), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JUNE, 2), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 26), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 19), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 12), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MAY, 5), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 28), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 21), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 14), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 31), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.MARCH, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(belowThreshold), getDate(2015, Month.MARCH, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 24), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 17), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 10), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.FEBRUARY, 3), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 27), null, 1, PIZZA_HUT_PAYE_REF))
        incomes.add(new Income(new BigDecimal(aboveThreshold), getDate(2015, Month.JANUARY, 20), null, 1, PIZZA_HUT_PAYE_REF))
        incomes
    }


    static def getEmployments() {
        List<Employments> employments = new ArrayList()
        employments.add(new Employments(new Employer(PIZZA_HUT, PIZZA_HUT_PAYE_REF)));
        employments.add(new Employments(new Employer(BURGER_KING, BURGER_KING_PAYE_REF)));
        employments

    }


    static def getDate(int year, Month month, int day) {
        LocalDate localDate = LocalDate.of(year,month,day)
        return localDate
    }

    static def subtractDaysFromDate(LocalDate date, long days) {
        return date.minusDays(days);
    }
}
