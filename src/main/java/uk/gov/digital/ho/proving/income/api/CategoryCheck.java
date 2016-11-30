package uk.gov.digital.ho.proving.income.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CategoryCheck {

    private String category;
    private boolean passed;

    // @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT")
    private LocalDate applicationRaisedDate;

    // @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT")
    private LocalDate assessmentStartDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FinancialCheckValues failureReason;

    private BigDecimal threshold;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CategoryCheck(String category, boolean passed, FinancialCheckValues failureReason, LocalDate applicationRaisedDate, LocalDate assessmentStartDate, BigDecimal threshold) {
        this.category = category;
        this.passed = passed;
        this.applicationRaisedDate = applicationRaisedDate;
        this.assessmentStartDate = assessmentStartDate;
        this.failureReason = failureReason;
        this.threshold = threshold;
    }

    private String formatDate(LocalDate date){
        return date.format(formatter);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getApplicationRaisedDate() {
        return formatDate(applicationRaisedDate);
    }

    public void setApplicationRaisedDate(LocalDate applicationRaisedDate) {
        this.applicationRaisedDate = applicationRaisedDate;
    }

    public String getAssessmentStartDate() {
        return formatDate(assessmentStartDate);
    }

    public void setAssessmentStartDate(LocalDate assessmentStartDate) {
        this.assessmentStartDate = assessmentStartDate;
    }

    public FinancialCheckValues getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(FinancialCheckValues failureReason) {
        this.failureReason = failureReason;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "CategoryCheck{" +
            "category='" + category + '\'' +
            ", passed=" + passed +
            ", applicationRaisedDate=" + applicationRaisedDate +
            ", assessmentStartDate=" + assessmentStartDate +
            ", failureReason=" + failureReason +
            ", threshold=" + threshold +
            '}';
    }
}
