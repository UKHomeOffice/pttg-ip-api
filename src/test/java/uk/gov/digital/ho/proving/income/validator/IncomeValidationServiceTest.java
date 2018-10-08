package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.api.domain.CheckedIndividual;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.CATB_NON_SALARIED_PASSED;
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.MONTHLY_SALARIED_PASSED;

@RunWith(MockitoJUnitRunner.class)
public class IncomeValidationServiceTest {

    private IncomeValidationService incomeValidationService;

    @Mock
    private CatASalariedIncomeValidator catASalariedIncomeValidator;

    @Mock
    private CatBNonSalariedIncomeValidator catBNonSalariedIncomeValidator;

    @Before
    public void setUp() {
        incomeValidationService = new IncomeValidationService(newArrayList(catASalariedIncomeValidator, catBNonSalariedIncomeValidator));
    }

    @Test
    public void thatAllValidatorsAreCalled() {
        IncomeValidationResult catAResult = getResult(MONTHLY_SALARIED_PASSED, "A");
        IncomeValidationResult catBResult = getResult(CATB_NON_SALARIED_PASSED, "B");

        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catBResult);

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        incomeValidationService.validate(request);

        verify(catASalariedIncomeValidator).validate(request);
        verify(catBNonSalariedIncomeValidator).validate(request);
    }

    @Test
    public void thatCategoryIsReturned() {
        IncomeValidationResult catAResult = getResult(MONTHLY_SALARIED_PASSED, "A");
        IncomeValidationResult catBResult = getResult(CATB_NON_SALARIED_PASSED, "B");

        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catBResult);

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

        IncomeValidationResult catAResult = getResultWithStartDate(MONTHLY_SALARIED_PASSED, "A", assessmentStartDate);
        IncomeValidationResult catBResult = getResultWithStartDate(CATB_NON_SALARIED_PASSED, "B", assessmentStartDate);

        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catBResult);

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

        IncomeValidationResult catAResult = getResultWithIndividual(MONTHLY_SALARIED_PASSED, "A",checkedIndividual);
        IncomeValidationResult catBResult = getResultWithIndividual(CATB_NON_SALARIED_PASSED, "B", checkedIndividual);

        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catBResult);

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.get(0).failureReason()).isEqualTo(MONTHLY_SALARIED_PASSED)
            .withFailMessage("The validation status should be as returned from the validator");
        assertThat(categoryChecks.get(0).threshold()).isEqualTo(BigDecimal.TEN)
            .withFailMessage("The threshold should be as returned from the validator");
        assertThat(categoryChecks.get(0).individuals().get(0).employers()).contains("Employer1").contains("Employer2")
            .withFailMessage("The employers should be as returned from the validator");

    }

    @Test
    public void thatEmploymentCheckPassIsOverriddenByCategoryBNonSalariedCheck() {
        IncomeValidationResult catAResult = getResult(MONTHLY_SALARIED_PASSED, "A");
        IncomeValidationResult catBResult = getResult(CATB_NON_SALARIED_PASSED, "B");

        when(catASalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catAResult);
        when(catBNonSalariedIncomeValidator.validate(any(IncomeValidationRequest.class))).thenReturn(catBResult);

        IncomeValidationRequest request = new IncomeValidationRequest(new ArrayList<>(), LocalDate.now(), 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.get(1).passed()).isTrue()
            .withFailMessage("The category B check should fail if the employment check fails");
        assertThat(categoryChecks.get(1).failureReason()).isEqualTo(CATB_NON_SALARIED_PASSED)
            .withFailMessage("The category B check should indicate employment check failed");
    }

    private IncomeValidationResult getResult(IncomeValidationStatus status, String category) {
        return IncomeValidationResult.builder()
            .status(status)
            .threshold(BigDecimal.TEN)
            .individuals(new ArrayList<>())
            .assessmentStartDate(LocalDate.now())
            .category(category)
            .calculationType("Calc type")
            .build();
    }

    private IncomeValidationResult getResultWithStartDate(IncomeValidationStatus status, String category, LocalDate assessmentStartDate) {
        return IncomeValidationResult.builder()
            .status(status)
            .threshold(BigDecimal.TEN)
            .individuals(new ArrayList<>())
            .assessmentStartDate(assessmentStartDate)
            .category(category)
            .calculationType("Calc type")
            .build();
    }

    private IncomeValidationResult getResultWithIndividual(IncomeValidationStatus status, String category, CheckedIndividual checkedIndividual) {
        return IncomeValidationResult.builder()
            .status(status)
            .threshold(BigDecimal.TEN)
            .individuals(ImmutableList.of(checkedIndividual))
            .assessmentStartDate(LocalDate.now())
            .category(category)
            .calculationType("Calc type")
            .build();
    }

}
