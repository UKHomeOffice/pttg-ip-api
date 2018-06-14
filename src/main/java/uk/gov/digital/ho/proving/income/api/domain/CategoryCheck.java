package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonProperty(value = "category")
    private String category;
    @JsonProperty(value = "passed")
    private boolean passed;
    @JsonProperty(value = "applicationRaisedDate")
    private LocalDate applicationRaisedDate;
    @JsonProperty(value = "assessmentStartDate")
    private LocalDate assessmentStartDate;
    @JsonProperty(value = "failureReason")
    private FinancialCheckValues failureReason;
    @JsonProperty(value = "threshold")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal threshold;
    @JsonProperty(value = "individuals")
    private List<CheckedIndividual> individuals;
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
