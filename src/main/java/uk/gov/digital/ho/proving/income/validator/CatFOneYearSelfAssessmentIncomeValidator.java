package uk.gov.digital.ho.proving.income.validator;

import org.springframework.stereotype.Service;
import uk.gov.digital.ho.proving.income.api.SelfAssessmentThresholdCalculator;
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn;
import uk.gov.digital.ho.proving.income.validator.domain.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

@Service
public class CatFOneYearSelfAssessmentIncomeValidator implements IncomeValidator {

    private static final String CATEGORY = "F";
    private static final String CALCULATION_TYPE = "Category F Self-Assessment Income";

    private final Clock clock;

    public CatFOneYearSelfAssessmentIncomeValidator(Clock clock) {
        this.clock = clock;
    }

    private static boolean isFromTaxYear(AnnualSelfAssessmentTaxReturn annualSelfAssessmentTaxReturn, TaxYear taxYear) {
        String rawTaxYear = annualSelfAssessmentTaxReturn.taxYear();
        TaxYear returnTaxYear = TaxYear.of(rawTaxYear);

        return returnTaxYear.equals(taxYear);
    }

    @Override
    public IncomeValidationResult validate(IncomeValidationRequest incomeValidationRequest) {
        List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns = getAnnualSelfAssessmentTaxReturns(incomeValidationRequest);

        BigDecimal threshold = new SelfAssessmentThresholdCalculator(incomeValidationRequest.dependants()).threshold();

        IncomeValidationStatus status = IncomeValidationStatus.SELF_ASSESSMENT_ONE_YEAR_FAILED;

        if (!previousYearsTaxReturns.isEmpty()) {
            BigDecimal totalSelfAssessmentProfit = getTotalIncome(previousYearsTaxReturns);

            boolean isSufficientIncome = totalSelfAssessmentProfit.compareTo(threshold) > 0;
            if (isSufficientIncome) {
                status = IncomeValidationStatus.SELF_ASSESSMENT_ONE_YEAR_PASSED;
            }
        }

        return IncomeValidationResult.builder()
            .status(status)
            .threshold(threshold)
            .individuals(incomeValidationRequest.getCheckedIndividuals())
            .assessmentStartDate(previousTaxYear().start())
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
        ApplicantIncome applicantIncome = incomeValidationRequest.applicantIncome();

        List<AnnualSelfAssessmentTaxReturn> previousYearsTaxReturns = getPreviousYearsTaxReturns(applicantIncome);

        if (incomeValidationRequest.isJointRequest()) {
            ApplicantIncome partnerIncome = incomeValidationRequest.partnerIncome();
            previousYearsTaxReturns.addAll(getPreviousYearsTaxReturns(partnerIncome));
        }

        return previousYearsTaxReturns;
    }

    private List<AnnualSelfAssessmentTaxReturn> getPreviousYearsTaxReturns(ApplicantIncome applicantIncome) {
        List<AnnualSelfAssessmentTaxReturn> annualSelfAssessmentTaxReturns = applicantIncome.incomeRecord().selfAssessment();

        TaxYear previousTaxYear = previousTaxYear();
        annualSelfAssessmentTaxReturns.removeIf(selfAssessmentReturn -> !isFromTaxYear(selfAssessmentReturn, previousTaxYear));

        if (annualSelfAssessmentTaxReturns.size() > 4) {
            throw new IllegalArgumentException(String.format("Should never have more than four tax returns in a year, got %d", annualSelfAssessmentTaxReturns.size()));
        }

        return annualSelfAssessmentTaxReturns;
    }

    private TaxYear previousTaxYear() {
        TaxYear currentTaxYear = TaxYear.from(clock);
        return currentTaxYear.previousTaxYear();
    }
}
