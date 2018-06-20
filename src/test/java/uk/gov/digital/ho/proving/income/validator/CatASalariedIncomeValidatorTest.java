package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.validator.TestData.*;

@RunWith(MockitoJUnitRunner.class)
public class CatASalariedIncomeValidatorTest {

    @InjectMocks
    private CatASalariedIncomeValidator catASalariedIncomeValidator;

    @Mock(name = "catASalariedMonthlyIncomeValidator")
    private IncomeValidator monthlyValidator;
    @Mock(name = "catASalariedWeeklyIncomeValidator")
    private IncomeValidator weeklyValidator;
    @Mock(name = "catAUnsupportedIncomeValidator")
    private IncomeValidator unsupportedValidator;

    @Test
    public void thatMonthlyPaymentsCallMonthlyValidator() {

        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);
        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        final IncomeValidationResult result = new IncomeValidationResult(IncomeValidationStatus.MONTHLY_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), raisedDate.minusMonths(6), "Calc type");
        when(monthlyValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

        catASalariedIncomeValidator.validate(request);

        verify(monthlyValidator).validate(request);
        verifyZeroInteractions(weeklyValidator);
        verifyZeroInteractions(unsupportedValidator);
    }

    @Test
    public void thatWeeklyPaymentsCallWeeklyValidator() {

        LocalDate raisedDate = getDate(2015, Month.AUGUST, 16);
        List<ApplicantIncome> incomes = getIncomesAboveThreshold2();
        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        final IncomeValidationResult result = new IncomeValidationResult(IncomeValidationStatus.WEEKLY_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), raisedDate.minusWeeks(26), "Calc type");
        when(monthlyValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

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

        final IncomeValidationResult result = new IncomeValidationResult(IncomeValidationStatus.UNKNOWN_PAY_FREQUENCY, BigDecimal.TEN, new ArrayList<>(), raisedDate.minusMonths(6), "Calc type");
        when(monthlyValidator.validate(any(IncomeValidationRequest.class))).thenReturn(result);

        catASalariedIncomeValidator.validate(request);

        verifyZeroInteractions(monthlyValidator);
        verifyZeroInteractions(weeklyValidator);
        verify(unsupportedValidator).validate(request);
    }


}
