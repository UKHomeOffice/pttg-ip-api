package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatBNonSalariedTestData.PIZZA_HUT_PAYE_REF;
import static uk.gov.digital.ho.proving.income.validator.CatBSharedTestData.*;

public class CatBSalariedTestData {

    static List<ApplicantIncome> twelveMonthsOverThreshold(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

        for (int i = 0; i < 12; i++) {
            incomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdApplicantInJoint(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

        for (int i = 0; i < 12; i++) {
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(1), 11, null, PIZZA_HUT_PAYE_REF));

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdPartnerInJoint(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

        applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(1), 11, null, PIZZA_HUT_PAYE_REF));
        for (int i = 0; i < 12; i++) {
            partnerIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }

        List<ApplicantIncome> jointIncomes = new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return ImmutableList.copyOf(jointIncomes);
    }

    static List<ApplicantIncome> twelveMonthsOverThresholdCombinedInJoint(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().divide(amount("2")).add(amount("0.01"));

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

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

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

    static List<ApplicantIncome> jointApplicationMonthMissingOneApplicant(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

        for (int i = 0; i < 12; i++) {
            if (i == 3) {
                partnerIncomes.add(new Income(monthlyIncome.multiply(amount("2")), applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
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

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        return new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
    }

    static List<ApplicantIncome> monthMissingButEnoughPayments(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().divide(amount("2")).add(amount("0.01"));
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                continue;
            }
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
            applicantIncomes.add(new Income(monthlyIncome, applicationRaisedDate.minusMonths(i), 12 - i, null, PIZZA_HUT_PAYE_REF));
        }
        return new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
    }

    static List<ApplicantIncome> mixedFrequencyButOverThreshold(final LocalDate applicationRaisedDate) {
        List<Income> applicantIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().divide(amount("2")).add(amount("0.01"));
        BigDecimal weeklyIncome = monthlyIncome.divide(amount("2"));

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

        return new ArrayList<>(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
    }
}
