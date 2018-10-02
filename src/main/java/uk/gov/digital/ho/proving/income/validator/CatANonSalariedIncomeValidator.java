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
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

@Service
public class CatANonSalariedIncomeValidator implements ActiveIncomeValidator {

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        BigDecimal threshold = new IncomeThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold();

        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());
        IncomeValidationRequest applicantOnlyRequest = incomeValidationRequest.toApplicantOnly();
        List<CheckedIndividual> checkedIndividuals = applicantOnlyRequest.getCheckedIndividuals();
        IncomeValidationStatus validationStatus = validateIncome(applicantOnlyRequest, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);


        if (validationStatus.isPassed() && !checkAllSameEmployer(getAllPayeIncomes(applicantOnlyRequest))) {
            return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold, checkedIndividuals);
        }

        if (!validationStatus.isPassed() && incomeValidationRequest.isJointRequest()) {
            IncomeValidationRequest partnerOnlyRequest = incomeValidationRequest.toPartnerOnly();
            checkedIndividuals = partnerOnlyRequest.getCheckedIndividuals();
            validationStatus = validateIncome(partnerOnlyRequest, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);
            if (validationStatus.isPassed() && !checkAllSameEmployer(getAllPayeIncomes(partnerOnlyRequest))) {
                return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold, checkedIndividuals);
            }

            if (!validationStatus.isPassed()) {
                validationStatus = validateIncome(incomeValidationRequest, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);
                checkedIndividuals = incomeValidationRequest.getCheckedIndividuals();

                if (validationStatus.isPassed() && !checkAllSameEmployerJointApplication(incomeValidationRequest)) {
                    return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold, checkedIndividuals);
                }
            }
        }

        return validationResult(validationStatus, assessmentStartDate, threshold, checkedIndividuals);
    }

    private IncomeValidationStatus validateIncome(IncomeValidationRequest validationRequest, LocalDate assessmentStartDate, LocalDate applicationRaisedDate, BigDecimal threshold) {
        List<Income> paye = getAllPayeIncomes(validationRequest);
        paye = removeDuplicates(filterIncomesByDates(paye, assessmentStartDate, applicationRaisedDate));

        if (paye.size() <= 0) {
            return NOT_ENOUGH_RECORDS;
        }

        if (checkValuePassesThreshold(paye.stream().map(Income::payment).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(2)), threshold)) {
            return CATA_NON_SALARIED_PASSED;
        }
        return CATA_NON_SALARIED_BELOW_THRESHOLD;
    }

    private IncomeValidationResult validationResult(IncomeValidationStatus validationStatus, LocalDate assessmentStartDate, BigDecimal threshold, List<CheckedIndividual> checkedIndividuals) {
        return IncomeValidationResult.builder()
            .status(validationStatus)
            .category("A")
            .calculationType("Category A non salaried")
            .assessmentStartDate(assessmentStartDate)
            .threshold(threshold)
            .individuals(checkedIndividuals)
            .build();
    }

}
