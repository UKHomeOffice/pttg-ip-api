package uk.gov.digital.ho.proving.income.validator.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.digital.ho.proving.income.validator.CatASalariedTestData.getDate;
import static uk.gov.digital.ho.proving.income.validator.CatBNonSalariedTestData.multipleApplicantSingleMonthEqualsNoDependantsThreshold;
import static uk.gov.digital.ho.proving.income.validator.CatBNonSalariedTestData.singleMonthlyPaymentEqualsNoDependantsThreshold;

public class IncomeValidationRequestTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void jointRequestIsRecognised() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        assertThat(request.isJointRequest())
            .withFailMessage("The request should be a joint request")
            .isTrue();
    }

    @Test
    public void singleRequestIsRecognised() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        assertThat(request.isJointRequest())
            .withFailMessage("The request should not be a joint request")
            .isFalse();
    }

    @Test
    public void toApplicantIncomeWithNoApplicantsFails() {
        thrown.expect(IllegalStateException.class);

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = new ArrayList<>();

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        request.toApplicantOnly();
    }

    @Test
    public void toApplicantIncomeReturnsFirstApplicantOnly() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        IncomeValidationRequest applicantRequest = request.toApplicantOnly();

        assertThat(applicantRequest.allIncome().size())
            .withFailMessage("There should only be a single applicant")
            .isEqualTo(1);

        ApplicantIncome applicantIncome = applicantRequest.applicantIncome();
        assertThat(applicantIncome.applicant().nino())
            .withFailMessage("The applicant nino should be the same as the first applicant on the joint request")
            .isEqualTo(request.applicantIncome().applicant().nino());
    }

    @Test
    public void toPartnerIncomeWithSingleApplicantFails() {
        thrown.expect(IllegalStateException.class);

        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = singleMonthlyPaymentEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        request.toPartnerOnly();
    }

    @Test
    public void toPartnerIncomeReturnsSecondApplicantOnly() {
        LocalDate raisedDate = getDate(2018, Month.SEPTEMBER, 23);
        List<ApplicantIncome> incomes = multipleApplicantSingleMonthEqualsNoDependantsThreshold(raisedDate);

        IncomeValidationRequest request = new IncomeValidationRequest(incomes, raisedDate, 0);

        IncomeValidationRequest partnerRequest = request.toPartnerOnly();

        assertThat(partnerRequest.allIncome().size())
            .withFailMessage("There should only be a single applicant")
            .isEqualTo(1);

        ApplicantIncome applicantIncome = partnerRequest.applicantIncome();
        assertThat(applicantIncome.applicant().nino())
            .withFailMessage("The partner's nino should be the same as the second applicant on the joint request")
            .isEqualTo(request.partnerIncome().applicant().nino());

    }
}
