package uk.gov.digital.ho.proving.income.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedIncomeValidator.getAssessmentStartDate;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.*;

@RunWith(MockitoJUnitRunner.class)
public class CatASalariedIncomeValidatorTest {

    @InjectMocks
    private CatASalariedIncomeValidator catASalariedIncomeValidator;

    @Mock
    private IncomeValidator monthlyValidator;
    @Mock
    private IncomeValidator weeklyValidator;
    @Mock
    private IncomeValidator unsupportedValidator;

    @Before
    public void setUp() {
        catASalariedIncomeValidator = new CatASalariedIncomeValidator(monthlyValidator, weeklyValidator, unsupportedValidator);
    }

    @Test
    public void thatMonthlyPaymentsCallMonthlyValidator() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        final IncomeValidationResult result = IncomeValidationResult.builder()
            .status(IncomeValidationStatus.MONTHLY_SALARIED_PASSED)
            .threshold(BigDecimal.TEN)
            .individuals(new ArrayList<>())
            .assessmentStartDate(raisedDate.minusMonths(6))
            .category("X")
            .calculationType("Calc type")
            .build();
        when(monthlyValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

        catASalariedIncomeValidator.validate(request);

        verify(monthlyValidator).validate(request);
        verifyZeroInteractions(weeklyValidator);
        verifyZeroInteractions(unsupportedValidator);
    }

    @Test
    public void thatWeeklyPaymentsCallWeeklyValidator() {

        LocalDate raisedDate = LocalDate.now();
        List<ApplicantIncome> incomes = getIncomesAboveThreshold2(raisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        final IncomeValidationResult result = IncomeValidationResult.builder()
            .status(IncomeValidationStatus.WEEKLY_SALARIED_PASSED)
            .threshold(BigDecimal.TEN)
            .individuals(new ArrayList<>())
            .assessmentStartDate(raisedDate.minusWeeks(26))
            .category("X")
            .calculationType("Calc type")
            .build();

        when(weeklyValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

        catASalariedIncomeValidator.validate(request);

        verifyZeroInteractions(monthlyValidator);
        verify(weeklyValidator).validate(request);
        verifyZeroInteractions(unsupportedValidator);
    }

    @Test
    public void thatUnsupportedPaymentFrequencyCallUnsupportedValidator() {

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);
        List<ApplicantIncome> incomes = fortnightlyPayment(raisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        final IncomeValidationResult result = IncomeValidationResult.builder()
            .status(IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY)
            .threshold(BigDecimal.TEN)
            .individuals(new ArrayList<>())
            .assessmentStartDate(raisedDate.minusMonths(6))
            .category("X")
            .calculationType("Calc type")
            .build();

        when(unsupportedValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

        catASalariedIncomeValidator.validate(request);

        verifyZeroInteractions(monthlyValidator);
        verifyZeroInteractions(weeklyValidator);
        verify(unsupportedValidator).validate(request);
    }

    @Test
    public void getAssessmentStartDateShouldBe6MonthsAgo() {
        LocalDate applicationRaisedDate = LocalDate.of(2018, Month.SEPTEMBER, 6);
        assertThat(getAssessmentStartDate(applicationRaisedDate)).isEqualTo(LocalDate.of(2018, Month.MARCH, 6));
    }

    @Test
    public void getAssessmentStartDateShouldBe6MonthsAgoAugust31st() {
        LocalDate applicationRaisedDate = LocalDate.of(2018, Month.AUGUST, 31);
        assertThat(getAssessmentStartDate(applicationRaisedDate)).isEqualTo(LocalDate.of(2018, Month.FEBRUARY, 28));
    }

    @Test
    public void getAssessmentStartDateShouldBe6MonthsAgoAugust31stLeapYear() {
        LocalDate applicationRaisedDate = LocalDate.of(2020, Month.AUGUST, 31);
        assertThat(getAssessmentStartDate(applicationRaisedDate)).isEqualTo(LocalDate.of(2020, Month.FEBRUARY, 29));
    }
}
