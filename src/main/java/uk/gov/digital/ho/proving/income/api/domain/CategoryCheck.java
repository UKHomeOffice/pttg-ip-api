package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class CategoryCheck {
    @JsonProperty
    private String category;
    @JsonProperty
    private boolean passed;
    @JsonProperty
    private LocalDate applicationRaisedDate;
    @JsonProperty
    private LocalDate assessmentStartDate;
    @JsonProperty
    private FinancialCheckValues failureReason;
    @JsonProperty
    private BigDecimal threshold;
    @JsonProperty
    private List<CheckedIndividual> individuals;
    @JsonProperty
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getApplicationRaisedDate() {
        return formatDate(applicationRaisedDate);
    }

    public String getAssessmentStartDate() {
        return formatDate(assessmentStartDate);
    }

    public static final String formatDate(LocalDate date){
        return date.format(DATE_TIME_FORMATTER);
    }

}
