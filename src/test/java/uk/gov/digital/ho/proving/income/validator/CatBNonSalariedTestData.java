package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatBSharedTestData.*;

public class CatBNonSalariedTestData {

    static List<ApplicantIncome> singleMonthlyPaymentAboveNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleMonthlyPaymentBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> multipleMonthlyPaymentsBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome1 = thresholdCalculator.getMonthlyThreshold().subtract(amount("50.00"));
        BigDecimal monthlyIncome2 = thresholdCalculator.getMonthlyThreshold().add(amount("49.99"));

        incomes.add(new Income(monthlyIncome1, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome2, raisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> multipleMonthlyPaymentsEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome1 = thresholdCalculator.getMonthlyThreshold().subtract(amount("50.00"));
        BigDecimal monthlyIncome2 = thresholdCalculator.getMonthlyThreshold().add(amount("50.00"));

        incomes.add(new Income(monthlyIncome1, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome2, raisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleMonthlyPaymentEqualsSingleDependantThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(1);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleMonthlyPaymentBelowSingleDependantThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(1);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleMonthlyPaymentEqualsThreeDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(3);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleMonthlyPaymentBelowThreeDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(3);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> multipleMonthlyPaymentSameMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().divide(new BigDecimal(4));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> multipleApplicantSingleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        List<Income> partnerIncomes = new ArrayList<>();
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    static List<ApplicantIncome> multipleApplicantBothSingleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    static List<ApplicantIncome> multipleApplicantBothSingleMonthBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)).subtract(amount("0.01")), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    static List<ApplicantIncome> multipleApplicantMultipleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(7), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF));
        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    static List<ApplicantIncome> multipleApplicantsOnlyApplicantEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.subtract(amount("0.01")), raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    static List<ApplicantIncome> multipleApplicantsOnlyPartnerEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.subtract(amount("0.01")), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

}
