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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.*;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.*;

@Service
public class CatANonSalariedIncomeValidator implements ActiveIncomeValidator {

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        BigDecimal threshold = new IncomeThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold();

        LocalDate assessmentStartDate = getAssessmentStartDate(incomeValidationRequest.applicationRaisedDate());
        List<Income> applicantPaye = incomeValidationRequest.applicantIncome().incomeRecord().paye();
        IncomeValidationStatus validationStatus = validateIncome(applicantPaye, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);

        IncomeValidationResult.IncomeValidationResultBuilder validationResultBuilder = IncomeValidationResult.builder();

        if (validationStatus.isPassed() && !checkAllSameEmployer(applicantPaye)) {
            return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold);
        }


        if (!validationStatus.isPassed() && incomeValidationRequest.isJointRequest()) {
            List<Income> partnerPaye = incomeValidationRequest.partnerIncome().incomeRecord().paye();
            validationStatus = (validateIncome(partnerPaye, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold));
            if (validationStatus.isPassed() && !checkAllSameEmployer(partnerPaye)) {
                return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold);
            }

            if (!validationStatus.isPassed()) {
                List<Income> allIncomes = new ArrayList<>(applicantPaye);
                allIncomes.addAll(partnerPaye);
                validationStatus = validateIncome(allIncomes, assessmentStartDate, incomeValidationRequest.applicationRaisedDate(), threshold);

                if (validationStatus.isPassed() && (!checkAllSameEmployer(applicantPaye) || !checkAllSameEmployer(partnerPaye))) {
                    return validationResult(MULTIPLE_EMPLOYERS, assessmentStartDate, threshold);
                }
            }
        }

        if (incomeValidationRequest.getCheckedIndividuals().size() > 0) {
            validationResultBuilder.individuals(singletonList(new CheckedIndividual(incomeValidationRequest.getCheckedIndividuals().get(0).nino(), emptyList())));
        }

        return validationResultBuilder
            .status(validationStatus)
            .category("A")
            .calculationType("Category A non salaried")
            .assessmentStartDate(assessmentStartDate)
            .threshold(threshold)
            .build();
    }

    private IncomeValidationStatus validateIncome(List<Income> paye, LocalDate assessmentStartDate, LocalDate applicationRaisedDate, BigDecimal threshold) {
        paye = removeDuplicates(filterIncomesByDates(paye, assessmentStartDate, applicationRaisedDate));

        if (paye.size() <= 0) {
            return NOT_ENOUGH_RECORDS;
        }

        if (checkValuePassesThreshold(paye.stream().map(Income::payment).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(2)), threshold)) {
            return CATA_NON_SALARIED_PASSED;
        }
        return CATA_NON_SALARIED_BELOW_THRESHOLD;
    }

    private IncomeValidationResult validationResult(IncomeValidationStatus validationStatus, LocalDate assessmentStartDate, BigDecimal threshold) {
        return IncomeValidationResult.builder()
            .status(validationStatus)
            .category("A")
            .calculationType("Category A non salaried")
            .assessmentStartDate(assessmentStartDate)
            .threshold(threshold)
            .build();
    }

}
