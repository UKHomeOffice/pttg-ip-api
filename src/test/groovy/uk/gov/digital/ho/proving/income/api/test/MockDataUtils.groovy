package uk.gov.digital.ho.proving.income.api.test

import uk.gov.digital.ho.proving.income.domain.Income
import uk.gov.digital.ho.proving.income.domain.Individual
import uk.gov.digital.ho.proving.income.domain.hmrc.Employer
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity

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

    static def getIdentity() {
        Identity identity = new Identity("Duncan", "Sinclair", LocalDate.of(1945, 4, 19), "AA123456A")
        identity
    }

    static def getConsecutiveIncomes() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15),PIZZA_HUT , "1400" ))
        incomes.add(new Income(getDate(2015, Month.MAY, 15),PIZZA_HUT , "1600" ))
        incomes.add(new Income(getDate(2015, Month.JUNE, 15),PIZZA_HUT , "1600" ))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15),PIZZA_HUT , "1600" ))
        incomes.add(new Income(getDate(2015, Month.JULY, 15),PIZZA_HUT , "1600" ))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 15),BURGER_KING , "1600" ))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15),PIZZA_HUT , "1600" ))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15),PIZZA_HUT , "1600" ))
        incomes
    }

    static def getConsecutiveIncomes2() {
        List<uk.gov.digital.ho.proving.income.domain.hmrc.Income> incomes = new ArrayList()
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1400"), getDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF ))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.FEBRUARY, 15), 1, null, BURGER_KING_PAYE_REF ))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes.add(new uk.gov.digital.ho.proving.income.domain.hmrc.Income(new BigDecimal("1600"), getDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF))
        incomes
    }

    static def getEmployments() {
        List<Employments> employments = new ArrayList()
        employments.add(new Employments(new Employer(PIZZA_HUT, PIZZA_HUT_PAYE_REF)));
        employments.add(new Employments(new Employer(BURGER_KING, BURGER_KING_PAYE_REF)));
        employments

    }
    static def getNotEnoughConsecutiveIncomes() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.MAY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JULY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1600"))
        incomes
    }

    static def getNoneConsecutiveIncomes() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 16), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.MAY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15), PIZZA_HUT, "1600"))
        incomes
    }

    static def getConsecutiveIncomesButDifferentEmployers() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.MAY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 15), BURGER_KING, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JULY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15), PIZZA_HUT, "1600"))
        incomes
    }

    static def getConsecutiveIncomesButLowAmounts() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.MAY, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JULY, 15), PIZZA_HUT, "1400"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1400"))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15), PIZZA_HUT, "1600"))
        incomes
    }

    static def getConsecutiveIncomesWithDifferentMonthlyPayDay() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15), PIZZA_HUT, "1400"))
        incomes.add(new Income(getDate(2015, Month.MAY, 16), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 17), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 15), BURGER_KING, "1600"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1600"))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15), PIZZA_HUT, "1600"))
        incomes
    }

    static def getConsecutiveIncomesWithExactlyTheAmount() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.JANUARY, 15), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.MAY, 16), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.JUNE, 17), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.APRIL, 15), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 15), BURGER_KING, "1550"))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 15), PIZZA_HUT, "1550"))
        incomes.add(new Income(getDate(2015, Month.SEPTEMBER, 15), PIZZA_HUT, "1550"))
        incomes
    }

    static def getIncomesAboveThreshold() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 2), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 26), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 19), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 12), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 5), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 27), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 20), PIZZA_HUT, aboveThreshold))
        incomes
    }

    static def getIncomesOnTheThreshold() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 2), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 26), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 19), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 12), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 5), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 28), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 21), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 14), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 10), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 3), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 27), PIZZA_HUT, weeklyThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 20), PIZZA_HUT, weeklyThreshold))
        incomes
    }


    static def getIncomesBelowThreshold() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 2), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 26), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 19), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 12), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 5), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 27), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 20), PIZZA_HUT, aboveThreshold))
        incomes
    }


    static def getIncomesExactly26AboveThreshold() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 2), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 26), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 19), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 12), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 5), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, aboveThreshold))

        incomes
    }


    static def getIncomesNotEnoughWeeks() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.JUNE,2), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.MAY,26), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.MAY,19), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.MAY,12), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.MAY,5), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.APRIL,28), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.APRIL,21), PIZZA_HUT, aboveThreshold))
//        incomes.add(new Income(getDate(2015,Month.APRIL,14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 27), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 20), PIZZA_HUT, aboveThreshold))

        incomes
    }


    static def getIncomesSomeBelowThreshold() {
        List<Income> incomes = new ArrayList()
        incomes.add(new Income(getDate(2015, Month.AUGUST, 11), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.AUGUST, 4), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JULY, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 30), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 23), PIZZA_HUT, belowThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 16), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 9), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JUNE, 2), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 26), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 19), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 12), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MAY, 5), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 28), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 21), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 14), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.APRIL, 7), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 31), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.MARCH, 3), PIZZA_HUT, belowThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 24), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 17), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 10), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.FEBRUARY, 3), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 27), PIZZA_HUT, aboveThreshold))
        incomes.add(new Income(getDate(2015, Month.JANUARY, 20), PIZZA_HUT, aboveThreshold))
        incomes
    }


    static def getDate(int year, Month month, int day) {
        LocalDate localDate = LocalDate.of(year,month,day)
        return localDate
    }

    static def subtractDaysFromDate(LocalDate date, long days) {
        return date.minusDays(days);
    }
}
