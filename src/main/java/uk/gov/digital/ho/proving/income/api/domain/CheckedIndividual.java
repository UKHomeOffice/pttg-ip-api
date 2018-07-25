package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uk.gov.digital.ho.proving.income.hmrc.domain.Employments;
import uk.gov.digital.ho.proving.income.validator.domain.ApplicantIncome;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public class CheckedIndividual {
    @JsonProperty(value = "nino")
    private String nino;

    @JsonProperty(value = "employers")
    private List<String> employers;

    public static CheckedIndividual fromApplicantIncome(ApplicantIncome applicantIncome) {
        String nino = applicantIncome.applicant().nino();
        List<Employments> employments = applicantIncome.employments();

        return new CheckedIndividual(nino, toEmployerNames(employments));
    }

    private static List<String> toEmployerNames(List<Employments> employments) {
        return employments.stream()
            .map(employment -> employment.employer().name())
            .distinct()
            .collect(Collectors.toList());
    }
}
