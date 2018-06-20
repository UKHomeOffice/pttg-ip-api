package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeValidationServiceTest {

    @InjectMocks
    private IncomeValidationService incomeValidationService;

    @Mock
    IncomeValidator catASalariedIncomeValidator;
    @Mock
    IncomeValidator catBNonSalariedIncomeValidator;

    @Test
    public void thatAllValidatorsAreCalled() {
        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.MONTHLY_SALARIED_PASSED, BigDecimal.ONE, new ArrayList<>(), LocalDate.now(), "Calc type"));
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), LocalDate.now(), "Calc type"));

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        incomeValidationService.validate(request);

        verify(catASalariedIncomeValidator).validate(request);
        verify(catBNonSalariedIncomeValidator).validate(request);
    }

    @Test
    public void thatCategoryIsReturned() {
        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.MONTHLY_SALARIED_PASSED, BigDecimal.ONE, new ArrayList<>(), LocalDate.now(), "Calc type"));
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), LocalDate.now(), "Calc type"));

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.size()).isEqualTo(2).withFailMessage("All validators should return a category check");
        assertThat(categoryChecks.get(0).category()).isEqualTo("A").withFailMessage("The category A check should return category 'A'");
        assertThat(categoryChecks.get(1).category()).isEqualTo("B").withFailMessage("The category B check should return category 'B'");
    }

    @Test
    public void thatDatesAreReturned() {
        final LocalDate assessmentStartDate = LocalDate.now().minusDays(2);
        final LocalDate applicationRaisedDate = LocalDate.now().minusDays(1);
        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.MONTHLY_SALARIED_PASSED, BigDecimal.ONE, new ArrayList<>(), assessmentStartDate, "Calc type"));
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), assessmentStartDate, "Calc type"));

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), applicationRaisedDate, 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.get(0).assessmentStartDate()).isEqualTo(assessmentStartDate)
            .withFailMessage("The category A check should have correct assessment start date");
        assertThat(categoryChecks.get(1).assessmentStartDate()).isEqualTo(assessmentStartDate)
            .withFailMessage("The category B check should have correct assessment start date");
        assertThat(categoryChecks.get(0).applicationRaisedDate()).isEqualTo(applicationRaisedDate)
            .withFailMessage("The category A check should have correct application raised date");
        assertThat(categoryChecks.get(1).applicationRaisedDate()).isEqualTo(applicationRaisedDate)
            .withFailMessage("The category B check should have correct application raised date");
    }

    @Test
    public void thatValidationResultsAreReturned() {
        CheckedIndividual checkedIndividual = new CheckedIndividual("NINO", ImmutableList.of("Employer1", "Employer2"));
        IncomeValidationResult catAResult = new IncomeValidationResult(IncomeValidationStatus.MONTHLY_SALARIED_PASSED, BigDecimal.TEN, ImmutableList.of(checkedIndividual), LocalDate.now(), "Calc type");
        IncomeValidationResult catBResult = new IncomeValidationResult(IncomeValidationStatus.CATB_NON_SALARIED_PASSED, BigDecimal.TEN, new ArrayList<>(), LocalDate.now(), "Calc type");
        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class)))
            .thenReturn(catBResult);

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.get(0).failureReason()).isEqualTo(IncomeValidationStatus.MONTHLY_SALARIED_PASSED)
            .withFailMessage("The validation status should be as returned from the validator");
        assertThat(categoryChecks.get(0).threshold()).isEqualTo(BigDecimal.TEN)
            .withFailMessage("The threshold should be as returned from the validator");
        assertThat(categoryChecks.get(0).individuals().get(0).employers()).contains("Employer1").contains("Employer2")
            .withFailMessage("The employers should be as returned from the validator");


    }

}
