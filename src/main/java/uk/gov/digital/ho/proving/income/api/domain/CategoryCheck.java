package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationResult;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class CategoryCheck {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @JsonProperty(value = "category")
    private String category;
    @JsonProperty(value = "calculationType")
    private String calculationType;
    @JsonProperty(value = "passed")
    private boolean passed;
    @JsonProperty(value = "applicationRaisedDate")
    private LocalDate applicationRaisedDate;
    @JsonProperty(value = "assessmentStartDate")
    private LocalDate assessmentStartDate;
    @JsonProperty(value = "failureReason")
    private IncomeValidationStatus failureReason;
    @JsonProperty(value = "threshold")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal threshold;
    @JsonProperty(value = "individuals")
    private List<CheckedIndividual> individuals;

    private static String formatDate(LocalDate date) {
        return date.format(DATE_TIME_FORMATTER);
    }

    public static CategoryCheck from(IncomeValidationResult result, LocalDate applicationRaisedDate) {
        return new CategoryCheck(
            result.category(),
            result.calculationType(),
            result.status().isPassed(),
            applicationRaisedDate,
            result.assessmentStartDate(),
            result.status(),
            result.threshold(),
            result.individuals());
    }

    public String getApplicationRaisedDate() {
        return formatDate(applicationRaisedDate);
    }

    public String getAssessmentStartDate() {
        return formatDate(assessmentStartDate);
    }
}
