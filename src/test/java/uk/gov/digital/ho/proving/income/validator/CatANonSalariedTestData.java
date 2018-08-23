package uk.gov.digital.ho.proving.income.validator;

import uk.gov.digital.ho.proving.income.hmrc.domain.Income;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.digital.ho.proving.income.validator.CatASharedTestData.*;

class CatANonSalariedTestData {

    static List<ApplicantIncome> sixMonthsOfSufficientNonSalariedIncome(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2200"), applicationRaisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("600"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("2600"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1300"), applicationRaisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sixMonthsOfInsufficientNonSalariedIncome(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("1000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1200"), applicationRaisedDate.minusMonths(4), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("600"), applicationRaisedDate.minusMonths(2), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("100"), applicationRaisedDate.minusMonths(1), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("1900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sixMonthsOfSufficientNonSalariedIncomeWithGaps(final LocalDate applicationRaisedDate) {
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income(amount("4000"), applicationRaisedDate.minusMonths(5), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("3100"), applicationRaisedDate.minusMonths(3), 1, null, PIZZA_HUT_PAYE_REF));
        incomes.add(new Income(amount("5900"), applicationRaisedDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }

    static List<ApplicantIncome> sufficientNonSalariedSingleIncome(final LocalDate incomeDate){
        List<Income> incomes = Collections.singletonList(new Income(amount("10000"), incomeDate, 1, null, PIZZA_HUT_PAYE_REF));
        return getApplicantIncomes(incomes, CatASharedTestData.PIZZA_HUT_EMPLOYER);
    }
}
