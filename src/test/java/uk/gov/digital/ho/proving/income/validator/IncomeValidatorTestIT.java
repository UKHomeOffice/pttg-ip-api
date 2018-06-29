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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.contiguousMonthlyPayments;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.getDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    IncomeValidationService.class,
    CatASalariedIncomeValidator.class,
    CatASalariedMonthlyIncomeValidator.class,
    CatASalariedWeeklyIncomeValidator.class,
    CatAUnsupportedIncomeValidator.class,
    CatBNonSalariedIncomeValidator.class,
    EmploymentCheckIncomeValidator.class
})
public class IncomeValidatorTestIT {

    @Autowired
    private IncomeValidationService incomeValidationService;

    @SpyBean(CatASalariedIncomeValidator.class)
    private IncomeValidator catASalariedIncomeValidator;
    @SpyBean(CatASalariedMonthlyIncomeValidator.class)
    private IncomeValidator catASalariedMonthlyIncomeValidator;
    @SpyBean(CatASalariedWeeklyIncomeValidator.class)
    private IncomeValidator catASalariedWeeklyIncomeValidator;
    @SpyBean(CatAUnsupportedIncomeValidator.class)
    private IncomeValidator catAUnsupportedIncomeValidator;
    @SpyBean(CatBNonSalariedIncomeValidator.class)
    private IncomeValidator catBNonSalariedIncomeValidator;
    @SpyBean(EmploymentCheckIncomeValidator.class)
    private IncomeValidator employmentCheckIncomeValidator;

    @Test
    public void thatAllCategoryChecksArePerformed() {
        LocalDate raisedDate = getDate(2015, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = contiguousMonthlyPayments(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);
        List<CategoryCheck> categoryChecks = incomeValidationService.validate(request);

        assertThat(categoryChecks.size()).isEqualTo(2)
            .withFailMessage("There should be 2 category checks performed");
        assertThat(categoryChecks.get(0).category()).isEqualTo("A")
            .withFailMessage("The category A check should return a result");
        assertThat(categoryChecks.get(1).category()).isEqualTo("B")
            .withFailMessage("The category B check should return a result");

        verify(catASalariedIncomeValidator).validate(request);
        verify(catASalariedMonthlyIncomeValidator).validate(request);
        verify(employmentCheckIncomeValidator).validate(request);
        verify(catBNonSalariedIncomeValidator).validate(request);

        verifyZeroInteractions(catASalariedWeeklyIncomeValidator);
        verifyZeroInteractions(catAUnsupportedIncomeValidator);

    }

}
