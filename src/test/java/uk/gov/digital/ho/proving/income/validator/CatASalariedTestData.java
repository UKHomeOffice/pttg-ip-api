package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class CatASalariedTestData {

    final static String PIZZA_HUT = "Pizza Hut";
    final static String BURGER_KING = "Burger King";
    final static String PIZZA_HUT_PAYE_REF = "Pizza Hut/ref";
    final static String BURGER_KING_PAYE_REF = "Burger King/ref";
    final static Employer PIZZA_HUT_EMPLOYER = new Employer(PIZZA_HUT, PIZZA_HUT_PAYE_REF);
    final static Employer BURGER_KING_EMPLOYER = new Employer(BURGER_KING, BURGER_KING_PAYE_REF);

    final static String weeklyThreshold = "357.69";
    final static String aboveThreshold = "357.70";
    final static String belowThreshold = "357.68";

    final static String NINO = "AA123456A";

    final static LocalDate DOB = LocalDate.of(1970, Month.JANUARY, 1);
    final static Applicant APPLICANT = new Applicant("Duncan", "Smith", DOB, NINO);
    final static HmrcIndividual HMRC_INDIVIDUAL = new HmrcIndividual("Duncan", "Smith", NINO, DOB);

    static BigDecimal amount(String i) {
        return new BigDecimal(i);
    }

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
        if (ref.equals(PIZZA_HUT_PAYE_REF)) {
            return PIZZA_HUT;
        }
        if (ref.equals(BURGER_KING_PAYE_REF)) {
            return BURGER_KING;
        }

        return ref;
    }

    private static List<ApplicantIncome> getApplicantIncomes(List<Income> paye, Employer... employers) {
        List<Employments> employments = Arrays.stream(employers).map(Employments::new).collect(Collectors.toList());
        IncomeRecord incomeRecord = new IncomeRecord(paye, new ArrayList<>(), employments, HMRC_INDIVIDUAL);
        return ImmutableList.of(new ApplicantIncome(APPLICANT, incomeRecord));
    }

    public static List<ApplicantIncome> contiguousMonthlyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsInOldestMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1666"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1666"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientRangeOfMonths(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), 2, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3), 3, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1666"), raisedDate.minusMonths(2), 4, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(2), 4, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 5, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 6, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1666"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1888"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1999"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("2000"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientQuantity(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1666"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1888"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1999"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1400"), paymentDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.FEBRUARY, 15), 1, null, BURGER_KING_PAYE_REF ));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MARCH, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    public static List<ApplicantIncome> getNoneConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JANUARY, 15), 10, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.FEBRUARY, 15), 11, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 16), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MAY, 15), 2, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JUNE, 15), 3, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.AUGUST, 15), 5, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 6, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getNotEnoughConsecutiveIncomes2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"),paymentDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"),paymentDate(2015, Month.JUNE, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"),paymentDate(2015, Month.APRIL, 15),1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"),paymentDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"),paymentDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesButDifferentEmployers2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JANUARY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MAY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JUNE, 15), 1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JULY, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.AUGUST, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesButLowAmounts2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JANUARY, 15),  10, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MAY, 15),  2, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JUNE, 15),  3, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 15),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1400"), paymentDate(2015, Month.JULY, 15),  4, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1400"), paymentDate(2015, Month.AUGUST, 15),  5, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15),  6, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomesWithDifferentMonthlyPayDay2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1400"), paymentDate(2015, Month.JANUARY, 15),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.MAY, 16),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JUNE, 17),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.APRIL, 15),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.JULY, 14),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.FEBRUARY, 15),  1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.AUGUST, 15),  1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), paymentDate(2015, Month.SEPTEMBER, 15),  1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }


    public static List<ApplicantIncome> getConsecutiveIncomesWithExactlyTheAmount2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.JANUARY, 15), 10, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.MAY, 16), 2, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.JUNE, 17), 3, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.APRIL, 15), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.JULY, 14), 4, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.FEBRUARY, 15), 11, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.AUGUST, 15), 5, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), paymentDate(2015, Month.SEPTEMBER, 15), 6, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }


    public static List<ApplicantIncome> getIncomesAboveThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 46, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 44, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 43, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 42, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesAboveThresholdMultiplePaymentsOneWeek() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 46, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.FEBRUARY, 18), null, 45, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesAboveThresholdMultiplePaymentsSameDay() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 46, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 45, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static  List<ApplicantIncome> getIncomesMultiplePaymentsSameWeekBelowThreshold() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 46, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount("5"), paymentDate(2015, Month.FEBRUARY, 17), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("5"), paymentDate(2015, Month.FEBRUARY, 18), null, 45, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);

    }

    static List<ApplicantIncome> getIncomesMultiplePaymentsSameWeekDifferentEmployers() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 51, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 25), null, 50, BURGER_KING_PAYE_REF));

        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 46, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesExactly26AboveThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 56, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesNotEnoughWeeks2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 3), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 46, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 44, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 43, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 42, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getIncomesSomeBelowThreshold2() {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 11), null, 19, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.AUGUST, 4), null, 18, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 28), null, 17, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 21), null, 16, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 14), null, 15, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JULY, 7), null, 14, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 30), null, 13, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.JUNE, 23), null, 12, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 16), null, 11, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 9), null, 10, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JUNE, 2), null, 9, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 26), null, 8, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 19), null, 7, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 12), null, 6, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MAY, 5), null, 5, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 28), null, 4, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 21), null, 3, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 14), null, 2, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.APRIL, 7), null, 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 31), null, 52, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 24), null, 51, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 17), null, 50, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.MARCH, 10), null, 49, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), paymentDate(2015, Month.MARCH, 3), null, 48, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 24), null, 47, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 17), null, 46, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 10), null, 45, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.FEBRUARY, 3), null, 44, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 27), null, 43, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), paymentDate(2015, Month.JANUARY, 20), null, 42, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> fortnightlyPayment(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), null, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1).minusDays(14), null, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1).minusDays(28), null, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, new Employer("n/a", "n/a"));
    }

    public static List<ApplicantIncome> changedFrequencyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(2), null, 1, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, new Employer("n/a", "n/a"));
    }

    static List<ApplicantIncome> twoPaymentsSameMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF ));

        incomes.add(new Income(amount("1550.99"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("0.01"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static LocalDate getDate(int year, Month month, int day) {
        return LocalDate.of(year,month,day);
    }

    public static LocalDate subtractDaysFromDate(LocalDate date, long days) {
        return date.minusDays(days);
    }
}
