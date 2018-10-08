package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

@Service
public class CatANonSalariedIncomeValidator implements ActiveIncomeValidator {

    private static final String CALCULATION_TYPE = "Category A Non Salaried";
    private static final String CATEGORY = "A";

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        BigDecimal threshold = new IncomeThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold();
        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());

        IncomeValidationRequest applicantOnlyRequest = incomeValidationRequest.toApplicantOnly();

        IncomeValidationStatus validationStatus = validateIncome(applicantOnlyRequest, assessmentStartDate, applicantOnlyRequest.applicationRaisedDate(), threshold);
        if (validationStatus.isPassed()) {
            return validationResult(validationStatus, assessmentStartDate, threshold, applicantOnlyRequest.getCheckedIndividuals());
        }

        if (incomeValidationRequest.isJointRequest()) {
            IncomeValidationRequest partnerOnlyRequest = incomeValidationRequest.toPartnerOnly();
            validationStatus = validateIncome(partnerOnlyRequest, assessmentStartDate, partnerOnlyRequest.applicationRaisedDate(), threshold);
            if (validationStatus.isPassed()) {
                return validationResult(validationStatus, assessmentStartDate, threshold, partnerOnlyRequest.getCheckedIndividuals());
            }

            validationStatus = validateJointIncome(incomeValidationRequest, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);
        }

        return validationResult(validationStatus, assessmentStartDate, threshold, incomeValidationRequest.getCheckedIndividuals());
    }

    private IncomeValidationStatus validateIncome(IncomeValidationRequest validationRequest, LocalDate assessmentStartDate, LocalDate applicationRaisedDate, BigDecimal threshold) {
        List<Income> paye = getAllPayeIncomes(validationRequest);
        paye = removeDuplicates(filterIncomesByDates(paye, assessmentStartDate, applicationRaisedDate));

        if (paye.isEmpty()) {
            return NOT_ENOUGH_RECORDS;
        }

        BigDecimal annualApplicantIncome = annualisedIncome(largestSingleEmployerIncome(paye));
        if (checkValuePassesThreshold(annualApplicantIncome, threshold)) {
            return CATA_NON_SALARIED_PASSED;
        }

        BigDecimal annualAllEmployerIncome = annualisedIncome(totalPayment(paye));
        if (checkValuePassesThreshold(annualAllEmployerIncome, threshold)) {
            return MULTIPLE_EMPLOYERS;
        }
        return CATA_NON_SALARIED_BELOW_THRESHOLD;
    }

    private IncomeValidationStatus validateJointIncome(IncomeValidationRequest validationRequest, LocalDate assessmentStartDate, LocalDate applicationRaisedDate, BigDecimal threshold) {

        List<Income> applicantPaye = validationRequest.applicantIncome().incomeRecord().paye();
        applicantPaye = removeDuplicates(filterIncomesByDates(applicantPaye, assessmentStartDate, applicationRaisedDate));

        List<Income> partnerPaye = validationRequest.partnerIncome().incomeRecord().paye();
        partnerPaye = removeDuplicates(filterIncomesByDates(partnerPaye, assessmentStartDate, applicationRaisedDate));

        List<Income> allPaye = new ArrayList<>(applicantPaye);
        allPaye.addAll(partnerPaye);

        if (allPaye.isEmpty()) {
            return NOT_ENOUGH_RECORDS;
        }

        BigDecimal applicantIncome = largestSingleEmployerIncome(applicantPaye);
        BigDecimal partnerIncome = largestSingleEmployerIncome(partnerPaye);
        BigDecimal annualCombinedIncome = annualisedIncome(applicantIncome.add(partnerIncome));

        if (checkValuePassesThreshold(annualCombinedIncome, threshold)) {
            return CATA_NON_SALARIED_PASSED;
        }

        BigDecimal annualAllEmployerCombinedIncome = annualisedIncome(totalPayment(allPaye));
        if (checkValuePassesThreshold(annualAllEmployerCombinedIncome, threshold)) {
            return MULTIPLE_EMPLOYERS;
        }
        return CATA_NON_SALARIED_BELOW_THRESHOLD;
    }

    private IncomeValidationResult validationResult(IncomeValidationStatus validationStatus, LocalDate assessmentStartDate, BigDecimal threshold, List<CheckedIndividual> checkedIndividuals) {
        return IncomeValidationResult.builder()
            .status(validationStatus)
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .assessmentStartDate(assessmentStartDate)
            .threshold(threshold)
            .individuals(checkedIndividuals)
            .build();
    }

    private BigDecimal annualisedIncome(BigDecimal sixMonthlyIncome) {
        return sixMonthlyIncome.multiply(BigDecimal.valueOf(2));
    }
}
