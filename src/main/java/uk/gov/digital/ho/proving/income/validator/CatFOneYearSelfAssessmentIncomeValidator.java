package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SelfAssessmentThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CatFOneYearSelfAssessmentIncomeValidator implements ActiveIncomeValidator {

    private static final String CATEGORY = "F";
    private static final String CALCULATION_TYPE = "Category F Self-Assessment Income";

    private final SelfAssessmentThresholdCalculator selfAssessmentThresholdCalculator = new SelfAssessmentThresholdCalculator();

    private static boolean isFromTaxYear(AnnualSelfAssessmentTaxReturn annualSelfAssessmentTaxReturn, TaxYear taxYear) {
        String rawTaxYear = annualSelfAssessmentTaxReturn.taxYear();
        TaxYear returnTaxYear = TaxYear.of(rawTaxYear);

        return returnTaxYear.equals(taxYear);
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns = getAnnualSelfAssessmentTaxReturns(incomeValidationRequest);
        BigDecimal threshold = selfAssessmentThresholdCalculator.threshold(incomeValidationRequest.dependants());

        IncomeValidationStatus status = IncomeValidationStatus.SELF_ASSESSMENT_ONE_YEAR_FAILED;

        if (!previousYearsTaxReturns.isEmpty()) {
            BigDecimal totalSelfAssessmentProfit = getTotalIncome(previousYearsTaxReturns);

            boolean hasSufficientIncome = totalSelfAssessmentProfit.compareTo(threshold) >= 0;
            if (hasSufficientIncome) {
                status = IncomeValidationStatus.SELF_ASSESSMENT_ONE_YEAR_PASSED;
            }
        }

        LocalDate applicationRaisedDate = incomeValidationRequest.applicationRaisedDate();
        return IncomeValidationResult.builder()
            .status(status)
            .threshold(threshold)
            .individuals(incomeValidationRequest.getCheckedIndividuals())
            .assessmentStartDate(previousTaxYear(applicationRaisedDate).start())
            .category(CATEGORY)
            .calculationType(CALCULATION_TYPE)
            .build();
    }

    private BigDecimal getTotalIncome(List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns) {
        return previousYearsTaxReturns.stream()
            .map(AnnualSelfAssessmentTaxReturn::selfEmploymentProfit)
            .reduce(BigDecimal::add)
            .get();
    }

    private List<AnnualSelfAssessmentTaxReturn> getAnnualSelfAssessmentTaxReturns(IncomeValidationRequest incomeValidationRequest) {
        LocalDate applicationRaisedDate = incomeValidationRequest.applicationRaisedDate();
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns = getPreviousYearsTaxReturns(applicantIncome, applicationRaisedDate);

        if (incomeValidationRequest.isJointRequest()) {
            ApplicantIncome partnerIncome = incomeValidationRequest.partnerIncome();
            previousYearsTaxReturns.addAll(getPreviousYearsTaxReturns(partnerIncome, applicationRaisedDate));
        }

        return previousYearsTaxReturns;
    }

    private List<AnnualSelfAssessmentTaxReturn> getPreviousYearsTaxReturns(ApplicantIncome applicantIncome, LocalDate applicationRaisedDate) {
        List<AnnualSelfAssessmentTaxReturn> annualSelfAssessmentTaxReturns = applicantIncome.incomeRecord().selfAssessment();

        TaxYear previousTaxYear = previousTaxYear(applicationRaisedDate);
        annualSelfAssessmentTaxReturns.removeIf(selfAssessmentReturn -> !isFromTaxYear(selfAssessmentReturn, previousTaxYear));

        if (annualSelfAssessmentTaxReturns.size() > 4) {
            throw new IllegalArgumentException(String.format("Should never have more than four tax returns in a year, got %d", annualSelfAssessmentTaxReturns.size()));
        }

        return annualSelfAssessmentTaxReturns;
    }

    private TaxYear previousTaxYear(LocalDate applicationRaisedDate) {
        TaxYear currentTaxYear = TaxYear.from(applicationRaisedDate);
        return currentTaxYear.previousTaxYear();
    }
}
