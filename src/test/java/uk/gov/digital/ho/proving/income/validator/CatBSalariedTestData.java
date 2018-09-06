package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatBSharedTestData.*;

class CatBSalariedTestData {

    static List<ApplicantIncome> twelveMonthsOverThreshold(final LocalDate applicationRaisedDate) {
        return twelveMonthsOverThreshold(applicationRaisedDate, 0);
    }

    static List<ApplicantIncome> twelveMonthsOverThreshold(final LocalDate applicationRaisedDate, final int dependants) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(dependants);

        for (int i = 0; i < 12; i++) {
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdNoPaymentThisMonth(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);

        for (int i = 0; i < 12; i++) {
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i + 1).withDayOfMonth(28), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdNotPaye(final LocalDate applicationRaisedDate) {
        List<AnnualSelfAssessmentTaxReturn> saIncomes = new ArrayList<>();
        BigDecimal saIncome = incomeOverMonthlyThreshold(0).multiply(amount("12"));

        saIncomes.add(new AnnualSelfAssessmentTaxReturn("2018", saIncome));

        IncomeRecord incomeRecord = new IncomeRecord(new ArrayList<>(), saIncomes, new ArrayList<>(), HMRC_INDIVIDUAL);
        return ImmutableList.of(new ApplicantIncome(APPLICANT, incomeRecord));
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdUnsorted(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);

        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(8), 4, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(11), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(6), 6, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(9), 3, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(5), 7, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(2), 10, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(4), 8, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(7), 5, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(3), 9, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate, 12, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(1), 11, null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> monthBelowThreshold(final LocalDate applicationRaisedDate) {
        return monthBelowThreshold(applicationRaisedDate, 0);
    }

    static List<ApplicantIncome> monthBelowThreshold(final LocalDate applicationRaisedDate, final int dependants) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(dependants);
        BigDecimal belowThresholdPayment = incomeUnderMonthlyThreshold(dependants);

        for (int i = 0; i < 12; i++) {
            if (i == 6) {
                incomes.add(new Income(belowThresholdPayment, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
                continue;
            }
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdJointApplication(final LocalDate applicationRaisedDate) {
        return twelveMonthsOverThresholdJointApplication(applicationRaisedDate, 0);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdJointApplication(final LocalDate applicationRaisedDate, final int dependants) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverThresholdJointApplication(dependants);

        for (int i = 0; i < 12; i++) {
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> twelveMonthsUnderThresholdJointApplication(final LocalDate applicationRaisedDate, final int dependants) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeBelowThresholdJointApplication(dependants);

        for (int i = 0; i < 12; i++) {
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdApplicantOnlyInJoint(final LocalDate applicationRaisedDate, final int dependants) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverThresholdJointApplication(dependants);

        for (int i = 0; i < 12; i++) {
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }


    static List<ApplicantIncome> jointApplicationMonthMissingBothApplicants(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);

        for (int i = 0; i < 12; i++) {
            if (i == 3) {
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> jointApplicationMonthUnderThreshold(final LocalDate applicationRaisedDate) {
        final int dependants = 0;
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverThresholdJointApplication(dependants);
        BigDecimal belowThreshold = incomeBelowThresholdJointApplication(dependants);

        for (int i = 0; i < 12; i++) {
            if (i == 1) {
                applicantIncomes.add(new Income(belowThreshold, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
                partnerIncomes.add(new Income(belowThreshold, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> monthMissingTooFewPayments(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        return getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> monthMissingButEnoughPayments(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverThresholdJointApplication(0);
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        return getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> mixedFrequencyButOverThreshold(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);
        BigDecimal weeklyIncome = monthlyIncome.divide(amount("2"), RoundingMode.FLOOR);

        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate, 12, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(1), 11, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(2), 10, null, PIZZA_HUT_PAYE_REF));

        applicantIncomes.add(new Income(weeklyIncome, applicationRaisedDate.minusMonths(3).withDayOfMonth(18), null, 16, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(weeklyIncome, applicationRaisedDate.minusMonths(3).withDayOfMonth(10), null, 14, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(weeklyIncome, applicationRaisedDate.minusMonths(3).withDayOfMonth(1), null, 13, PIZZA_HUT_PAYE_REF));

        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(4), 8, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(5), 7, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(6), 6, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(7), 5, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(8), 4, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(9), 3, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(11), 1, null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> overThresholdMultipleEmployers(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);

        for (int i = 0; i < 12; i++) {
            if (isEven(i)) {
                incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            } else {
                incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, BURGER_KING_PAYE_REF));
            }
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdButNotAllBeforeArd(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        BigDecimal monthlyIncome = incomeOverMonthlyThreshold(0);

        for (int i = 0; i < 8; i++) {
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i + 1), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        for (int i = 0; i < 4; i++) {
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.plusMonths(i), i, null, PIZZA_HUT_PAYE_REF));
        }
        assert incomes.size() == 12;
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    private static BigDecimal incomeOverMonthlyThreshold(int dependants) {
        return new IncomeThresholdCalculator(dependants).getMonthlyThreshold().add(amount("0.01"));
    }

    private static BigDecimal incomeUnderMonthlyThreshold(int dependants) {
        return new IncomeThresholdCalculator(dependants).getMonthlyThreshold().subtract(amount("0.01"));
    }

    private static BigDecimal incomeOverThresholdJointApplication(int dependants) {
        return new IncomeThresholdCalculator(dependants).getMonthlyThreshold().divide(amount("2"), RoundingMode.FLOOR).add(amount("0.01"));
    }

    private static BigDecimal incomeBelowThresholdJointApplication(int dependants) {
        return new IncomeThresholdCalculator(dependants).getMonthlyThreshold().divide(amount("2"), RoundingMode.FLOOR).subtract(amount("0.01"));
    }

    private static boolean isEven(int i) {
        return i % 2 == 0;
    }
}
