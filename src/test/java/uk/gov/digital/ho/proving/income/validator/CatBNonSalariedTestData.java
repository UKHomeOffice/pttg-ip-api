package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatBSharedTestData.getApplicantIncomes;
import static uk.gov.digital.ho.proving.income.validator.CatBSharedTestData.getPartnerIncomes;

public class CatBNonSalariedTestData {

    final static String PIZZA_HUT = "Pizza Hut";
    final static String BURGER_KING = "Burger King";
    final static String PIZZA_HUT_PAYE_REF = "Pizza Hut/ref";
    final static String BURGER_KING_PAYE_REF = "Pizza Hut/ref";

    public static List<ApplicantIncome> singleMonthlyPaymentAboveNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().add(CatBSharedTestData.amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(CatBSharedTestData.amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> multipleMonthlyPaymentsBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome1 = thresholdCalculator.getMonthlyThreshold().subtract(CatBSharedTestData.amount("50.00"));
        BigDecimal monthlyIncome2 = thresholdCalculator.getMonthlyThreshold().add(CatBSharedTestData.amount("49.99"));

        incomes.add(new Income(monthlyIncome1, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(monthlyIncome2, raisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> multipleMonthlyPaymentsEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome1 = thresholdCalculator.getMonthlyThreshold().subtract(CatBSharedTestData.amount("50.00"));
        BigDecimal monthlyIncome2 = thresholdCalculator.getMonthlyThreshold().add(CatBSharedTestData.amount("50.00"));

        incomes.add(new Income(monthlyIncome1, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(monthlyIncome2, raisedDate.minusMonths(10), 2, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentEqualsSingleDependantThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(1);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentBelowSingleDependantThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(1);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(CatBSharedTestData.amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentEqualsThreeDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(3);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> singleMonthlyPaymentBelowThreeDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(3);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().subtract(CatBSharedTestData.amount("0.01"));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> multipleMonthlyPaymentSameMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold().divide(new BigDecimal(4));

        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        incomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));

        return getApplicantIncomes(incomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER);
    }

    public static List<ApplicantIncome> multipleApplicantSingleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        List<Income> partnerIncomes = new ArrayList<>();
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    public static List<ApplicantIncome> multipleApplicantBothSingleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF ));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    public static List<ApplicantIncome> multipleApplicantBothSingleMonthBelowNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)).subtract(CatBSharedTestData.amount("0.01")), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF ));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    public static List<ApplicantIncome> multipleApplicantMultipleMonthEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        applicantIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(7), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(5), 1, null, BURGER_KING_PAYE_REF ));
        partnerIncomes.add(new Income(monthlyIncome.divide(new BigDecimal(2)), raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF ));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    public static List<ApplicantIncome> multipleApplicantsOnlyApplicantEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome.subtract(CatBSharedTestData.amount("0.01")), raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF ));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

    public static List<ApplicantIncome> multipleApplicantsOnlyPartnerEqualsNoDependantsThreshold(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantsIncomes = new ArrayList<>();

        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(0);
        BigDecimal monthlyIncome = thresholdCalculator.getMonthlyThreshold();

        applicantIncomes.add(new Income(monthlyIncome.subtract(CatBSharedTestData.amount("0.01")), raisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF ));
        allApplicantsIncomes.addAll(getApplicantIncomes(applicantIncomes, CatBSharedTestData.PIZZA_HUT_EMPLOYER));

        partnerIncomes.add(new Income(monthlyIncome, raisedDate.minusMonths(7), 1, null, BURGER_KING_PAYE_REF ));
        allApplicantsIncomes.addAll(getPartnerIncomes(partnerIncomes, CatBSharedTestData.BURGER_KING_EMPLOYER));

        return allApplicantsIncomes;
    }

}
