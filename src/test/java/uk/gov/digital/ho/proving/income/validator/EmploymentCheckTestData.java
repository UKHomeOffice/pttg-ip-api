package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmploymentCheckTestData {
    final static String PIZZA_HUT = "Pizza Hut";
    final static String BURGER_KING = "Burger King";
    private final static String PIZZA_HUT_PAYE_REF = "Pizza Hut/ref";
    private final static String BURGER_KING_PAYE_REF = "Pizza Hut/ref";
    private final static Employer PIZZA_HUT_EMPLOYER = new Employer(PIZZA_HUT, PIZZA_HUT_PAYE_REF);
    private final static Employer BURGER_KING_EMPLOYER = new Employer(BURGER_KING, BURGER_KING_PAYE_REF);

    final static String NINO = "AA123456A";
    final static String NINO_PARTNER = "BB123456B";

    private final static LocalDate DOB = LocalDate.of(1970, Month.JANUARY, 1);
    private final static Applicant APPLICANT = new Applicant("Duncan", "Smith", DOB, NINO);
    private final static Applicant PARTNER = new Applicant("Denise", "Smith", DOB, NINO_PARTNER);
    private final static HmrcIndividual HMRC_INDIVIDUAL = new HmrcIndividual("Duncan", "Smith", NINO, DOB);
    private final static HmrcIndividual HMRC_INDIVIDUAL_PARTNER = new HmrcIndividual("Denise", "Smith", NINO_PARTNER, DOB);

    private final static BigDecimal BASE_THRESHOLD = BigDecimal.valueOf(18600);
    private final static BigDecimal ONE_DEPENDANT_THRESHOLD = BigDecimal.valueOf(22400);
    private final static BigDecimal REMAINING_DEPENDANT_INCREMENT = BigDecimal.valueOf(2400);
    private final static IncomeThresholdCalculator thresholdCalculator = new IncomeThresholdCalculator(BASE_THRESHOLD, ONE_DEPENDANT_THRESHOLD, REMAINING_DEPENDANT_INCREMENT);

    static BigDecimal amount(String i) {
        return new BigDecimal(i);
    }

    private static List<ApplicantIncome> getApplicantIncomes(List<Income> paye, Employer... employers) {
        List<Employments> employments = Arrays.stream(employers).map(Employments::new).collect(Collectors.toList());
        IncomeRecord incomeRecord = new IncomeRecord(paye, new ArrayList<>(), employments, HMRC_INDIVIDUAL);
        return ImmutableList.of(new ApplicantIncome(APPLICANT, incomeRecord));
    }

    private static List<ApplicantIncome> getPartnerIncomes(List<Income> paye, Employer... employers) {
        List<Employments> employments = Arrays.stream(employers).map(Employments::new).collect(Collectors.toList());
        IncomeRecord incomeRecord = new IncomeRecord(paye, new ArrayList<>(), employments, HMRC_INDIVIDUAL_PARTNER);
        return ImmutableList.of(new ApplicantIncome(PARTNER, incomeRecord));
    }

    static List<ApplicantIncome> noIncome(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> noIncomeTwoApplicants(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> incomePriorToAssessmentStart(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate.minusDays(1), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleIncomeOnAssessmentStartDay(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1 - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleIncomeAfterAssessmentStartDay(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate.plusDays(1), 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> incomeBeforeAndAfterAssessmentStartDay(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate.minusDays(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> multipleIncomeOnAssessmentStartDay(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleIncomeBelowThresholdOnAssessmentStartDay(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0).subtract(BigDecimal.ONE);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleIncomeWithSingleDependantOnAssessmentStartDate(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(1);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singleIncomeWithTwoDependantsOnAssessmentStartDate(LocalDate raisedDate) {
        List<Income> incomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(2).min(BigDecimal.ONE);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        incomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> singlePartnerIncomeOnAssessmentStartDay(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        partnerIncomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> singlePartnerIncomeBeforeAssessmentStartDay(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        partnerIncomes.add(new Income(monthlyIncome, assessmentStartDate.minusDays(1), 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> jointIncomeOnAssessmentStartDay(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        applicantIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        partnerIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> jointIncomeBeforeAndAfterAssessmentStartDay(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(EmploymentCheckIncomeValidator.ASSESSMENT_START_DAYS_PREVIOUS - 1);

        applicantIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        partnerIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(2)), assessmentStartDate.minusDays(1), 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> jointIncomeMultiplePayments(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(5);

        applicantIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(4)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        applicantIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(4)), assessmentStartDate.plusDays(1), 1, null, PIZZA_HUT_PAYE_REF));
        partnerIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(4)), assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));
        partnerIncomes.add(new Income(monthlyIncome.divide(BigDecimal.valueOf(4)), assessmentStartDate.plusDays(1), 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

    static List<ApplicantIncome> jointIncomeApplicantOnlyProvidesIncome(LocalDate raisedDate) {
        List<ApplicantIncome> allApplicantIncomes = new ArrayList<>();
        List<Income> applicantIncomes = new ArrayList<>();
        List<Income> partnerIncomes = new ArrayList<>();

        BigDecimal monthlyIncome = thresholdCalculator.monthlyThreshold(0);

        LocalDate assessmentStartDate = raisedDate.minusDays(5);

        applicantIncomes.add(new Income(monthlyIncome, assessmentStartDate, 1, null, PIZZA_HUT_PAYE_REF));

        allApplicantIncomes.addAll(getApplicantIncomes(applicantIncomes, PIZZA_HUT_EMPLOYER));
        allApplicantIncomes.addAll(getPartnerIncomes(partnerIncomes, BURGER_KING_EMPLOYER));

        return allApplicantIncomes;
    }

}
