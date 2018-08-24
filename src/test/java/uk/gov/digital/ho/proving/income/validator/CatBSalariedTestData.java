package uk.gov.digital.ho.proving.income.validator;

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

        List<ApplicantIncome> jointIncomes = new ArrayList<>();
        jointIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return jointIncomes;
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

        List<ApplicantIncome> jointIncomes = new ArrayList<>();
        jointIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        jointIncomes.addAll(getPartnerIncomes(partnerIncomes, PIZZA_HUT_EMPLOYER));
        return jointIncomes;
    }
}
