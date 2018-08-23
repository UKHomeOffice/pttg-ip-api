package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class CatASalariedTestData {

    final static String weeklyThreshold = "357.69";
    final static String aboveThreshold = "357.70";
    final static String belowThreshold = "357.68";

    static LocalDate paymentDate(int year, Month month, int day) {
        return getDate(year, month, day);
    }

    static List<Employments> getEmployers(List<Income> incomes) {
        Set<String> employeRef = new HashSet<>();
        for (Income income : incomes) {
            employeRef.add(income.employerPayeReference());
        }
        return employeRef.stream().map(ref -> new Employments(new Employer(employerRefToName(ref), ref))).collect(Collectors.toList());
    }

    static String employerRefToName(String ref) {
        if (ref.equals(CatASharedTestData.PIZZA_HUT_PAYE_REF)) {
            return CatASharedTestData.PIZZA_HUT;
        }
        if (ref.equals(CatASharedTestData.BURGER_KING_PAYE_REF)) {
            return CatASharedTestData.BURGER_KING;
        }

        return ref;
    }

    public static List<ApplicantIncome> contiguousMonthlyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(4), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsInEarliestMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(4), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsInMiddleMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(4), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsInOldestMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(4), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientRangeOfMonths(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(4), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(3), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(2), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1888"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1999"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("2000"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientQuantity(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1666"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1777"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1888"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1999"), raisedDate.minusMonths(5), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate, 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1400"), paymentDate(2015, Month.JANUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.FEBRUARY, 15), 1, null, CatASharedTestData.BURGER_KING_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MARCH, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MAY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JUNE, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JULY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.AUGUST, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER, CatASharedTestData.BURGER_KING_EMPLOYER);
    }

    public static List<ApplicantIncome> getNoneConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JANUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.FEBRUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 16), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MAY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JUNE, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.AUGUST, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getNotEnoughConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"),paymentDate(2015, Month.MAY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"),paymentDate(2015, Month.JUNE, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"),paymentDate(2015, Month.APRIL, 15),1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"),paymentDate(2015, Month.JULY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"),paymentDate(2015, Month.AUGUST, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesButDifferentEmployers2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JANUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MAY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JUNE, 15), 1, null, CatASharedTestData.BURGER_KING_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JULY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.AUGUST, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER, CatASharedTestData.BURGER_KING_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesButLowAmounts2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JANUARY, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MAY, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JUNE, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1400"), paymentDate(2015, Month.JULY, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1400"), paymentDate(2015, Month.AUGUST, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesWithDifferentMonthlyPayDay2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1400"), paymentDate(2015, Month.JANUARY, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.MAY, 16),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JUNE, 17),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.APRIL, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.JULY, 14),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.FEBRUARY, 15),  1, null, CatASharedTestData.BURGER_KING_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.AUGUST, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15),  1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER, CatASharedTestData.BURGER_KING_EMPLOYER);
    }


    public static List<ApplicantIncome> getConsecutiveIncomesWithExactlyTheAmount2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.JANUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.MAY, 16), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.JUNE, 17), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.APRIL, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.JULY, 14), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.FEBRUARY, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.AUGUST, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount("1550"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }


    public static List<ApplicantIncome> getIncomesAboveThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesExactly26AboveThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));

        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesNotEnoughWeeks2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));

        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesSomeBelowThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(belowThreshold), paymentDate(2015, Month.JUNE, 23), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(belowThreshold), paymentDate(2015, Month.MARCH, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(CatASharedTestData.amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF));
        return CatASharedTestData.getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> fortnightlyPayment(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), null, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1).minusDays(14), null, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1).minusDays(28), null, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, new Employer("n/a", "n/a"));
    }

    public static List<ApplicantIncome> changedFrequencyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(1), 1, null, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(CatASharedTestData.amount("1600"), raisedDate.minusMonths(2), null, 1, CatASharedTestData.PIZZA_HUT_PAYE_REF ));
        return CatASharedTestData.getApplicantIncomes(incomes, new Employer("n/a", "n/a"));
    }

    public static LocalDate getDate(int year, Month month, int day) {
        return LocalDate.of(year,month,day);
    }

    public static LocalDate subtractDaysFromDate(LocalDate date, long days) {
        return date.minusDays(days);
    }
}
