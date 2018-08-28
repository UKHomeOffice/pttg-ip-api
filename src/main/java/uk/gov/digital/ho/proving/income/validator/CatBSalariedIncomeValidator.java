package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.isSuccessiveMonths;

@Service
public class CatBSalariedIncomeValidator implements ActiveIncomeValidator {

    private static final int INCOME_PERIOD_START_DATE_YEARS_AGO = 1;
    private static final String CALCULATION_TYPE = "Category B salaried";
    private static final String CATEGORY = "B";

    private final EmploymentCheckIncomeValidator employmentCheckIncomeValidator; // TODO OJR 2018/08/28 Check that employment check rules same for Cat B as Cat A

    public CatBSalariedIncomeValidator(EmploymentCheckIncomeValidator employmentCheckIncomeValidator) {
        this.employmentCheckIncomeValidator = employmentCheckIncomeValidator;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        IncomeValidationResult employmentCheckValidation = employmentCheckIncomeValidator.validate(incomeValidationRequest);
        if (!employmentCheckValidation.status().isPassed()) {
            return employmentCheckValidation;
        }

        IncomeValidationResult applicantResult = validateForApplicant(incomeValidationRequest);
        if (applicantResult.status().isPassed() || !incomeValidationRequest.isJointRequest()) {
            return applicantResult;
        }
        IncomeValidationResult partnerResult = validateForPartner(incomeValidationRequest);
        if (partnerResult.status().isPassed()) {
            return partnerResult;
        }
        return validateForJoint(incomeValidationRequest);
    }

    private LocalDate getApplicationStartDate(IncomeValidationRequest incomeValidationRequest) {
        return incomeValidationRequest.applicationRaisedDate().minusYears(INCOME_PERIOD_START_DATE_YEARS_AGO);
    }

    private IncomeValidationResult validateForApplicant(IncomeValidationRequest incomeValidationRequest) {
        return validateForIndividual(incomeValidationRequest, incomeValidationRequest.applicantIncome().incomeRecord().paye());
    }

    private IncomeValidationResult validateForPartner(IncomeValidationRequest incomeValidationRequest) {
        return validateForIndividual(incomeValidationRequest, incomeValidationRequest.partnerIncome().incomeRecord().paye());
    }

    private IncomeValidationResult validateForJoint(IncomeValidationRequest incomeValidationRequest) {
        List<Income> jointPaye = incomeValidationRequest.applicantIncome().incomeRecord().paye();
        jointPaye.addAll(incomeValidationRequest.partnerIncome().incomeRecord().paye());
        return validateForIndividual(incomeValidationRequest, jointPaye);
    }


    private IncomeValidationResult validateForIndividual(IncomeValidationRequest incomeValidationRequest, List<Income> paye) {
        if (paye.size() < 12) {
            return validationResult(incomeValidationRequest, IncomeValidationStatus.NOT_ENOUGH_RECORDS);
        }

        Map<Integer, List<Income>> collectedByMonth = paye.stream().collect(Collectors.groupingBy(Income::yearAndMonth));
        List<List<Income>> monthlyIncomes = new ArrayList<>();
        collectedByMonth.forEach((key, value) -> monthlyIncomes.add(value));
        monthlyIncomes.sort(Comparator.comparingInt(monthlyIncome -> monthlyIncome.get(0).yearAndMonth()));


        if (monthMissing(monthlyIncomes)) {
            return validationResult(incomeValidationRequest, IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
        }

        return validationResult(incomeValidationRequest, IncomeValidationStatus.CATB_SALARIED_PASSED);
    }

    private IncomeValidationResult validationResult(IncomeValidationRequest incomeValidationRequest, IncomeValidationStatus validationStatus) {
        return new IncomeValidationResult(
            validationStatus,
            new IncomeThresholdCalculator(incomeValidationRequest.dependants()).yearlyThreshold(),
            incomeValidationRequest.getCheckedIndividuals(),
            getApplicationStartDate(incomeValidationRequest),
            CATEGORY,
            CALCULATION_TYPE
        );
    }

    private boolean monthMissing(List<List<Income>> monthlyIncomes) {
        for (int i = 0; i < monthlyIncomes.size() - 1; i++) {
            if (!isSuccessiveMonths(monthlyIncomes.get(i + 1).get(0), monthlyIncomes.get(i).get(0))) {
                return true;
            }
        }
        return false;
    }
}
