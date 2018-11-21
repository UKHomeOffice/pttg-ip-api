package uk.gov.digital.ho.proving.income.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.proving.income.api.domain.CategoryCheck;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.contiguousMonthlyPayments;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.getDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IncomeValidatorTestIT {

    @Autowired
    private IncomeValidationService incomeValidationService;

    @SpyBean(CatASalariedIncomeValidator.class)
    private IncomeValidator catASalariedIncomeValidator;
    @SpyBean(CatASalariedMonthlyIncomeValidator.class)
    private IncomeValidator catASalariedMonthlyIncomeValidator;
    @SpyBean(CatASalariedWeeklyIncomeValidator.class)
    private IncomeValidator catASalariedWeeklyIncomeValidator;
    @SpyBean(CatANonSalariedIncomeValidator.class)
    private IncomeValidator catANonSalariedIncomeValidator;
    @SpyBean(CatAUnsupportedIncomeValidator.class)
    private IncomeValidator catAUnsupportedIncomeValidator;
    @SpyBean(CatBNonSalariedIncomeValidator.class)
    private IncomeValidator catBNonSalariedIncomeValidator;
    @SpyBean(CatBSalariedIncomeValidator.class)
    private IncomeValidator catBSalariedIncomeValidator;
    @SpyBean(EmploymentCheckIncomeValidator.class)
    private IncomeValidator employmentCheckIncomeValidator;
    @SpyBean(CatFOneYearSelfAssessmentIncomeValidator.class)
    private CatFOneYearSelfAssessmentIncomeValidator catFOneYearSelfAssessmentIncomeValidator;

    @Test
    public void thatAllCategoryChecksArePerformed() {
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.size())
            .withFailMessage("There should be 4 category checks performed")
            .isEqualTo(5);

        List<String> returnedCategories = categoryChecks.stream()
            .map(CategoryCheck::category)
            .collect(Collectors.toList());

        assertThat(returnedCategories)
            .withFailMessage("The category A check should return a result")
            .contains("A");

        assertThat(returnedCategories)
            .withFailMessage("The category B check should return a result")
            .contains("B");

        assertThat(returnedCategories)
            .withFailMessage("The category F check should return a result")
            .contains("F");

        verify(catASalariedIncomeValidator).validate(request);
        verify(catASalariedMonthlyIncomeValidator).validate(request);
        verify(catANonSalariedIncomeValidator).validate(request);
        verify(employmentCheckIncomeValidator, times(2)).validate(request); // Called for both CatB Salaried and Unsalaried
        verify(catBSalariedIncomeValidator).validate(request);
        verify(catBNonSalariedIncomeValidator).validate(request);
        verify(catFOneYearSelfAssessmentIncomeValidator).validate(request);

        verifyZeroInteractions(catASalariedWeeklyIncomeValidator);
        verifyZeroInteractions(catAUnsupportedIncomeValidator);
    }
}
