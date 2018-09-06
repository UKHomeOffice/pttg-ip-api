package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationRequest;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CatAMonthlyIncomeValidatorTestToo {
    private static final String ANY_EMPLOYER_PAYE_REF = "any employer PAYE ref";
    private static final LocalDate MIDDLE_OF_CURRENT_MONTH = LocalDate.now().withDayOfMonth(14);

    private CatASalariedMonthlyIncomeValidator service;

    private List<Employments> employments;
    private List<Employments> employmentsWithDuplicates;
    private List<AnnualSelfAssessmentTaxReturn> taxReturns;
    private List<Employments> multipleEmployments;
    private List<Income> incomeWithoutDuplicates;
    private List<Income> incomeWithDuplicates;

    @Before
    public void setup() {

        service = new CatASalariedMonthlyIncomeValidator();

        Income incomeA = incomeFromMonthsAgo(6);
        Income incomeB = incomeFromMonthsAgo(5);
        Income incomeC = incomeFromMonthsAgo(4);
        Income incomeD = incomeFromMonthsAgo(3);
        Income incomeE = incomeFromMonthsAgo(2);
        Income incomeF = incomeFromMonthsAgo(1);
        Income incomeG = incomeFromMonthsAgo(0);

        incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(incomeA);
        incomeWithoutDuplicates.add(incomeB);
        incomeWithoutDuplicates.add(incomeC);
        incomeWithoutDuplicates.add(incomeD);
        incomeWithoutDuplicates.add(incomeE);
        incomeWithoutDuplicates.add(incomeF);
        incomeWithoutDuplicates.add(incomeG);

        incomeWithDuplicates = new ArrayList<>();

        incomeWithDuplicates.add(incomeA);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeB);
        incomeWithDuplicates.add(incomeC);
        incomeWithDuplicates.add(incomeC);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeD);
        incomeWithDuplicates.add(incomeE);
        incomeWithDuplicates.add(incomeE);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeF);
        incomeWithDuplicates.add(incomeG);
        incomeWithDuplicates.add(incomeG);
        incomeWithDuplicates.add(incomeG);

        employments = new ArrayList<>();
        employments.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));

        taxReturns = new ArrayList<>();

        multipleEmployments = new ArrayList<>();
        multipleEmployments.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        multipleEmployments.add(new Employments(new Employer("another employer", ANY_EMPLOYER_PAYE_REF)));


        employmentsWithDuplicates = new ArrayList<>();
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
        employmentsWithDuplicates.add(new Employments(new Employer("any employer", ANY_EMPLOYER_PAYE_REF)));
    }

    private Income incomeFromMonthsAgo(int offset) {
        return new Income(new BigDecimal("1600"),
            MIDDLE_OF_CURRENT_MONTH.minusMonths(offset),
            1,
            null,
            ANY_EMPLOYER_PAYE_REF);
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentEqualToCurrentDayOfMonth() {

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, hmrcIndividual);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        LocalDate applicationRaisedDate = this.MIDDLE_OF_CURRENT_MONTH;
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);

        IncomeValidationResult result = service.validate(request);

        assertThat(result.status().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentBeforeCurrentDayOfMonth() {

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, hmrcIndividual);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        LocalDate applicationRaisedDate = MIDDLE_OF_CURRENT_MONTH.plusDays(1);
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);

        IncomeValidationResult result = service.validate(request);

        assertThat(result.status().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithoutDuplicatesAndDayInMonthOfPaymentAfterCurrentDayOfMonth() {

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, taxReturns, employments, hmrcIndividual);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        LocalDate applicationRaisedDate = MIDDLE_OF_CURRENT_MONTH.minusDays(1);
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);

        IncomeValidationResult result = service.validate(request);

        assertThat(result.status().isPassed()).isTrue();
    }

    @Test
    public void shouldPassWhenValidWithDuplicates() {

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomeWithDuplicates, taxReturns, employments, hmrcIndividual);
        LocalDate applicationRaisedDate = MIDDLE_OF_CURRENT_MONTH.minusDays(1);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);

        IncomeValidationResult result = service.validate(request);

        assertThat(result.status().isPassed()).isTrue();
    }

    @Test
    public void shouldFilterOutDuplicateEmployers() {

        List<Income> incomes = new ArrayList<>();

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomes, taxReturns, employmentsWithDuplicates, hmrcIndividual);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        LocalDate applicationRaisedDate = LocalDate.now().minusMonths(6);
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);

        IncomeValidationResult result = service.validate(request);

        assertThat(result.individuals().get(0).employers().size()).isEqualTo(1);
    }

    @Test
    public void duplicateEmployerFilterShouldAllowMultipleEmployersWithDifferentNames() {

        List<Income> incomes = new ArrayList<>();

        HmrcIndividual hmrcIndividual = aIndividual();
        Applicant applicant = new Applicant(hmrcIndividual.firstName(), hmrcIndividual.lastName(), LocalDate.now(), hmrcIndividual.nino());
        IncomeRecord incomeRecord = new IncomeRecord(incomes, taxReturns, multipleEmployments, hmrcIndividual);
        ApplicantIncome applicantIncome = new ApplicantIncome(applicant, incomeRecord);
        LocalDate applicationRaisedDate = LocalDate.now().minusMonths(6);
        IncomeValidationRequest request = new IncomeValidationRequest(ImmutableList.of(applicantIncome), applicationRaisedDate, 0);


        IncomeValidationResult result = service.validate(request);

        assertThat(result.individuals().get(0).employers().size()).isEqualTo(2);
    }

    private HmrcIndividual aIndividual() {
        return new HmrcIndividual("Joe", "Bloggs", "NE121212C", LocalDate.now());
    }
}
