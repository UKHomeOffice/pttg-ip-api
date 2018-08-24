package uk.gov.digital.ho.proving.income.validator;

import com.google.common.collect.ImmutableList;
import cucumber.api.java.cs.A;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.gov.digital.ho.proving.income.api.domain.Applicant;
import uk.gov.digital.ho.proving.income.hmrc.domain.*;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.digital.ho.proving.income.validator.CatASharedTestData.*;

class CatANonSalariedTestData {

    private static final String PARTNER_NINO = "KK998877B";
    private static final LocalDate PARTNER_DOB = LocalDate.of(1971, Month.AUGUST, 8);
    private static final HmrcIndividual HMRC_INDIVIDUAL_PARTNER = new HmrcIndividual("Eva", "Smith", PARTNER_NINO, PARTNER_DOB);
    public static final Applicant PARTNER = new Applicant("Eva", "Smith", PARTNER_DOB, PARTNER_NINO);

    static List<ApplicantIncome> sixMonthsOfSufficientNonSalariedIncome(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2200"), applicationRaisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("600"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2600"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1300"), applicationRaisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sixMonthsOfInsufficientNonSalariedIncome(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1200"), applicationRaisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("600"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("100"), applicationRaisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sixMonthsOfSufficientNonSalariedIncomeWithGaps(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("4000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("3100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("5900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sufficientNonSalariedSingleIncome(final LocalDate incomeDate) {
        List<Income> incomes = Collections.singletonList(new Income(amount("10000"), incomeDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sufficientNonSalariedButOnlyFor2Employers(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1500"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2700"), applicationRaisedDate.minusMonths(4), 1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("1100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2000"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1300"), applicationRaisedDate.minusMonths(1), 1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("1900"), applicationRaisedDate, 1, null, BURGER_KING_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> sufficientNonSalariedFor1Of2Employers(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1500"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2700"), applicationRaisedDate.minusMonths(4), 1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("1100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2000"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("3300"), applicationRaisedDate.minusMonths(1), 1, null, BURGER_KING_PAYE_REF));
        incomes.add(new Income(amount("4900"), applicationRaisedDate, 1, null, BURGER_KING_PAYE_REF));
        return getApplicantIncomes(incomes, PIZZA_HUT_EMPLOYER, BURGER_KING_EMPLOYER);
    }

    static List<ApplicantIncome> sufficientNonSalariedWithPartner(final LocalDate applicationRaisedDate) {
        List<Income> incomesForApplicant = new ArrayList<>();
        incomesForApplicant.add(new Income(amount("1000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomesForApplicant.add(new Income(amount("600"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomesForApplicant.add(new Income(amount("1300"), applicationRaisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomesForApplicant.add(new Income(amount("1900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));

        List<Income> incomesForPartner = new ArrayList<>();
        incomesForPartner.add(new Income(amount("2200"), applicationRaisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF));
        incomesForPartner.add(new Income(amount("2600"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));

        return getApplicantAndPartnerIncomes(incomesForApplicant, Collections.singletonList(PIZZA_HUT_EMPLOYER), incomesForPartner, Collections.singletonList(PIZZA_HUT_EMPLOYER));
    }

    private static List<ApplicantIncome> getApplicantAndPartnerIncomes(List<Income> applicantIncomes, List<Employer> applicantEmployers, List<Income> partnerIncomes, List<Employer> partnerEmployers) {
        List<Employments> applicantEmployments = applicantEmployers.stream().map(Employments::new).collect(Collectors.toList());
        IncomeRecord applicantIncomeRecord = new IncomeRecord(applicantIncomes, new ArrayList<>(), applicantEmployments, HMRC_INDIVIDUAL);

        List<Employments> partnerEmployments = partnerEmployers.stream().map(Employments::new).collect(Collectors.toList());
        IncomeRecord partnerIncomeRecord = new IncomeRecord(partnerIncomes, new ArrayList<>(), partnerEmployments, HMRC_INDIVIDUAL_PARTNER);

        return ImmutableList.of(new ApplicantIncome(APPLICANT, applicantIncomeRecord), new ApplicantIncome(PARTNER, partnerIncomeRecord));
    }
}
