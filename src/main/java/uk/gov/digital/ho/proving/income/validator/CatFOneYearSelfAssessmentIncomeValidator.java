package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CatFOneYearSelfAssessmentIncomeValidator implements ActiveIncomeValidator {

    private static final String CATEGORY = "F";
    private static final String CALCULATION_TYPE = "Category F Self-Assessment Income";

    private final IncomeThresholdCalculator incomeThresholdCalculator;

    public CatFOneYearSelfAssessmentIncomeValidator(IncomeThresholdCalculator incomeThresholdCalculator) {
        this.incomeThresholdCalculator = incomeThresholdCalculator;
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        IncomeValidationStatus status = IncomeValidationStatus.SELF_ASSESSMENT_ONE_YEAR_FAILED;

        BigDecimal threshold = incomeThresholdCalculator.yearlyThreshold(incomeValidationRequest.dependants());

        List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns = getSelfAssessmentReturnsFromPreviousTaxYear(incomeValidationRequest);
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

    private List<AnnualSelfAssessmentTaxReturn> getSelfAssessmentReturnsFromPreviousTaxYear(IncomeValidationRequest incomeValidationRequest) {
        LocalDate applicationRaisedDate = incomeValidationRequest.applicationRaisedDate();
        TaxYear previousTaxYear = previousTaxYear(applicationRaisedDate);

        return incomeValidationRequest.allIncome()
            .stream()
            .map(ApplicantIncome::incomeRecord)
            .flatMap(incomeRecord -> incomeRecord.selfAssessment().stream())
            .filter(annualSelfAssessmentTaxReturn -> isFromTaxYear(annualSelfAssessmentTaxReturn, previousTaxYear))
            .collect(toList());
    }

    private TaxYear previousTaxYear(LocalDate applicationRaisedDate) {
        TaxYear currentTaxYear = TaxYear.from(applicationRaisedDate);
        return currentTaxYear.previousTaxYear();
    }

    public boolean isFromTaxYear(AnnualSelfAssessmentTaxReturn annualSelfAssessmentTaxReturn, TaxYear taxYear) {
        String returnTaxYear = annualSelfAssessmentTaxReturn.taxYear();
        return TaxYear.of(returnTaxYear).equals(taxYear);
    }
}
