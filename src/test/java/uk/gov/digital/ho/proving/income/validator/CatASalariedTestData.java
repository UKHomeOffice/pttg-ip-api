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

    private static List<ApplicantIncome> getApplicantIncomes(List<Income> paye, Employer... employers) {
        List<Employments> employments = Arrays.stream(employers).map(Employments::new).collect(Collectors.toList());
        IncomeRecord incomeRecord = new IncomeRecord(paye, new ArrayList<>(), employments, HMRC_INDIVIDUAL);
        return ImmutableList.of(new ApplicantIncome(APPLICANT, incomeRecord));
    }

    static List<ApplicantIncome> contiguousMonthlyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            incomes.add(new Income(amount("1600"), raisedDate.minusMonths(i), 1, null, PIZZA_HUT_PAYE_REF));
        }
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> contiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientRangeOfMonths(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                continue;
            }
            incomes.add(new Income(amount("1600"), raisedDate.minusMonths(i), 6 - i, null, PIZZA_HUT_PAYE_REF));
        }
        incomes.add(new Income(amount("1666"), raisedDate.minusMonths(2), 4, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1777"), raisedDate.minusMonths(2), 4, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonth(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        String[] incomeAmounts = {"1666", "1777", "1888", "1999", "2000"};
        for (String incomeAmount : incomeAmounts) {
            incomes.add(new Income(amount(incomeAmount), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        }
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> nonContiguousMonthlyPaymentsWithMultiplePaymentsPerMonthAndInsufficientQuantity(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        String[] incomeAmounts = {"1666", "1777", "1888", "1999"};
        for (String incomeAmount : incomeAmounts) {
            incomes.add(new Income(amount(incomeAmount), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        }
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> getConsecutiveIncomes2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1400"), raisedDate.minusMonths(8), 1, null, PIZZA_HUT_PAYE_REF));
        for (int i = 0; i < 8; i++) {
            incomes.add(new Income(amount("1600"), raisedDate.minusMonths(i), 1, null, PIZZA_HUT_PAYE_REF));
        }
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> getNoneConsecutiveIncomes2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(8), getMonthNumber(raisedDate.minusMonths(8)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(7), getMonthNumber(raisedDate.minusMonths(7)), null, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(5)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5).withDayOfMonth(16), getMonthNumber(raisedDate.minusMonths(5)), null, PIZZA_HUT_PAYE_REF));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), getMonthNumber(raisedDate.minusMonths(4)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3), getMonthNumber(raisedDate.minusMonths(3)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), getMonthNumber(raisedDate.minusMonths(1)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(0), getMonthNumber(raisedDate.minusMonths(0)), null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getNotEnoughConsecutiveIncomes2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            incomes.add(new Income(amount("1600"), raisedDate.minusMonths(i), getMonthNumber(raisedDate.minusMonths(i)), null, PIZZA_HUT_PAYE_REF));
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getConsecutiveIncomesButDifferentEmployers2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String payeRef = PIZZA_HUT_PAYE_REF;
            if (i == 4) {
                payeRef = BURGER_KING_PAYE_REF;
            }
            incomes.add(new Income(amount("1600"), raisedDate.minusMonths(i), getMonthNumber(raisedDate.minusMonths(i)), null, payeRef));
        }
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> getConsecutiveIncomesButLowAmounts2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(8), getMonthNumber(raisedDate.minusMonths(8)), null, PIZZA_HUT_PAYE_REF));
        for (int i = 0; i < 6; i++) {
            BigDecimal incomeAmount = amount("1600");
            if (i == 1 || i == 2) {
                incomeAmount = amount("1400");
            }
            incomes.add(new Income(incomeAmount, raisedDate.minusMonths(i), getMonthNumber(raisedDate.minusMonths(i)), null, PIZZA_HUT_PAYE_REF));
        }
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getConsecutiveIncomesWithDifferentMonthlyPayDay2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1400"), raisedDate.minusMonths(8).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(8)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(7).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(7)), null, BURGER_KING_PAYE_REF));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(5)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4).withDayOfMonth(16), getMonthNumber(raisedDate.minusMonths(4)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3).withDayOfMonth(17), getMonthNumber(raisedDate.minusMonths(3)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(2).withDayOfMonth(14), getMonthNumber(raisedDate.minusMonths(2)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(1)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1600"), raisedDate.withDayOfMonth(15), getMonthNumber(raisedDate), null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }


    static List<ApplicantIncome> getConsecutiveIncomesWithExactlyTheAmount2(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(8).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(8)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(4).withDayOfMonth(16), getMonthNumber(raisedDate.minusMonths(4)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(3).withDayOfMonth(17), getMonthNumber(raisedDate.minusMonths(3)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(5).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(5)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(2).withDayOfMonth(14), getMonthNumber(raisedDate.minusMonths(2)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(7).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(7)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.minusMonths(1).withDayOfMonth(15), getMonthNumber(raisedDate.minusMonths(1)), null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1550"), raisedDate.withDayOfMonth(15), getMonthNumber(raisedDate), null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }


    static List<ApplicantIncome> getIncomesAboveThreshold2(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 30);
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesAboveThresholdMultiplePaymentsOneWeek(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 26);

        Income lastIncome = incomes.get(incomes.size() - 1);
        incomes.add(new Income(amount(belowThreshold), lastIncome.paymentDate().minusWeeks(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), lastIncome.paymentDate().minusWeeks(1).plusDays(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesAboveThresholdMultiplePaymentsSameDay(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 26);

        Income lastIncome = incomes.get(incomes.size() - 1);
        incomes.add(new Income(amount(belowThreshold), lastIncome.paymentDate().minusWeeks(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(belowThreshold), lastIncome.paymentDate().minusWeeks(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesMultiplePaymentsSameWeekBelowThreshold(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 25);

        Income lastIncome = incomes.get(incomes.size() - 1);
        incomes.add(new Income(amount("5"), lastIncome.paymentDate().minusWeeks(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("5"), lastIncome.paymentDate().minusWeeks(1).plusDays(1), null, lastIncome.weekPayNumber() - 1, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesMultiplePaymentsSameWeekDifferentEmployers(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 20);

        Income lastIncome = incomes.get(incomes.size() - 1);
        int weekPayNumber = decrementWeekNumber(lastIncome.weekPayNumber());
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(1), null, weekPayNumber, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(1).plusDays(1), null, weekPayNumber, BURGER_KING_PAYE_REF));

        weekPayNumber = decrementWeekNumber(weekPayNumber);
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(2), null, weekPayNumber, PIZZA_HUT_PAYE_REF));
        weekPayNumber = decrementWeekNumber(weekPayNumber);
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(3), null, weekPayNumber, PIZZA_HUT_PAYE_REF));
        weekPayNumber = decrementWeekNumber(weekPayNumber);
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(4), null, weekPayNumber, PIZZA_HUT_PAYE_REF));
        weekPayNumber = decrementWeekNumber(weekPayNumber);
        incomes.add(new Income(amount(aboveThreshold), lastIncome.paymentDate().minusWeeks(5), null, weekPayNumber, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesExactly26AboveThreshold2(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 26);
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesNotEnoughWeeks2(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 22);
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> getIncomesSomeBelowThreshold2(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 30);

        Income incomeToReplace = incomes.get(7);
        incomes.set(7, new Income(amount(belowThreshold), incomeToReplace.paymentDate(), null, incomeToReplace.weekPayNumber(), incomeToReplace.employerPayeReference()));

        incomeToReplace = incomes.get(23);
        incomes.set(23, new Income(amount(belowThreshold), incomeToReplace.paymentDate(), null, incomeToReplace.weekPayNumber(), incomeToReplace.employerPayeReference()));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> fortnightlyPayment(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), null, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1).minusDays(14), null, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1).minusDays(28), null, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, new Employer("n/a", "n/a"));
    }

    static List<ApplicantIncome> changedFrequencyPayments(LocalDate raisedDate) {
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

        incomes.add(new Income(amount("1549.99"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("0.01"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> incomeWithDuplicateMonthlyPayments(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF ));

        incomes.add(new Income(amount("1549.99"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1549.99"), raisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF ));

        incomes.add(new Income(amount("1600"), raisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(amount("1600"), raisedDate, 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> incomeWithDuplicateWeeklyPayments(LocalDate raisedDate) {
        List<Income> incomes = generateWeeklyIncomes(raisedDate, 26);

        Income incomeToReplace = incomes.get(16);
        incomes.set(16, new Income(amount(belowThreshold), incomeToReplace.paymentDate(), null, incomeToReplace.weekPayNumber(), incomeToReplace.employerPayeReference()));
        incomes.add(16, new Income(amount(belowThreshold), incomeToReplace.paymentDate(), null, incomeToReplace.weekPayNumber(), incomeToReplace.employerPayeReference()));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static LocalDate getDate(int year, Month month, int day) {
        return LocalDate.of(year,month,day);
    }

    private static int getMonthNumber(LocalDate paymentDate) {
        int taxMonth = paymentDate.getMonthValue() - 3;
        return taxMonth > 0 ? taxMonth : 12 + taxMonth;
    }

    private static List<Income> generateWeeklyIncomes(LocalDate raisedDate, int numberOfIncomes) {
        List<Income> incomes = new ArrayList<>();
        LocalDate paymentDate = raisedDate.minusDays(5);
        int weekNumber = 19;
        while (incomes.size() < numberOfIncomes) {
            incomes.add(new Income(amount(aboveThreshold), paymentDate, null, weekNumber, PIZZA_HUT_PAYE_REF));
            weekNumber = decrementWeekNumber(weekNumber);
            paymentDate = paymentDate.minusWeeks(1);
        }
        return incomes;
    }

    private static int decrementWeekNumber(int weekNumber) {
        weekNumber--;
        weekNumber = weekNumber > 0 ? weekNumber : 52;
        return weekNumber;
    }
}
