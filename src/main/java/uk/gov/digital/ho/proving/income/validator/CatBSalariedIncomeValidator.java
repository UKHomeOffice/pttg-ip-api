package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.IncomeValidationHelper.isSuccessiveMonths;

@Service
public class CatBSalariedIncomeValidator implements ActiveIncomeValidator {

    public static final int INCOME_PERIOD_START_DATE_YEARS_AGO = 1;
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

        LocalDate incomePeriodStartDate = getApplicationStartDate(incomeValidationRequest); // TODO OJR 2018/08/24 Check this cut off correct - probably a test

        IncomeValidationResult applicantResult = validateForApplicant(incomeValidationRequest, incomePeriodStartDate);
        if (applicantResult.status().isPassed() || !incomeValidationRequest.isJointRequest()) {
            return applicantResult;
        }
        IncomeValidationResult partnerResult = validateForPartner(incomeValidationRequest, incomePeriodStartDate);
        if (partnerResult.status().isPassed()) {
            return partnerResult;
        }
        return validateForJoint(incomeValidationRequest, incomePeriodStartDate);
    }

    private LocalDate getApplicationStartDate(IncomeValidationRequest incomeValidationRequest) {
        return incomeValidationRequest.applicationRaisedDate().minusYears(INCOME_PERIOD_START_DATE_YEARS_AGO);
    }

    private IncomeValidationResult validateForApplicant(IncomeValidationRequest incomeValidationRequest, LocalDate incomePeriodStartDate) {
        return validateForIndividual(incomeValidationRequest, incomePeriodStartDate, incomeValidationRequest.applicantIncome().incomeRecord().paye());
    }

    private IncomeValidationResult validateForPartner(IncomeValidationRequest incomeValidationRequest, LocalDate incomePeriodStartDate) {
        return validateForIndividual(incomeValidationRequest, incomePeriodStartDate, incomeValidationRequest.partnerIncome().incomeRecord().paye());
    }

    private IncomeValidationResult validateForJoint(IncomeValidationRequest incomeValidationRequest, LocalDate incomePeriodStartDate) {
        // TODO OJR 2018/08/24 This is probably not the way to do it as it's probably required that each individual has 12 months each
        // and it's just the added values that must be over the threshold
        List<Income> jointPaye = incomeValidationRequest.applicantIncome().incomeRecord().paye();
        jointPaye.addAll(incomeValidationRequest.partnerIncome().incomeRecord().paye());
        return validateForIndividual(incomeValidationRequest, incomePeriodStartDate, jointPaye);
    }


    private IncomeValidationResult validateForIndividual(IncomeValidationRequest incomeValidationRequest, LocalDate incomePeriodStartDate, List<Income> paye) {

        paye.sort(Comparator.comparingInt(Income::yearAndMonth));
        paye = paye.stream().filter(income -> !income.paymentDate().isBefore(incomePeriodStartDate)).collect(Collectors.toList());

        if (paye.size() < 12) {
            return validationResult(incomeValidationRequest, IncomeValidationStatus.NOT_ENOUGH_RECORDS);
        }
        if (monthMissing(paye)) {
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

    private boolean monthMissing(List<Income> incomeValidationRequest) {
        for (int i = 0; i < incomeValidationRequest.size() - 1; i++) {
            if (!isSuccessiveMonths(incomeValidationRequest.get(i + 1), incomeValidationRequest.get(i))) {
                return true;
            }
        }
        return false;
    }
}
