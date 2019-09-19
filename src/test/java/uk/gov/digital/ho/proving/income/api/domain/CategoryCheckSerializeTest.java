package uk.gov.digital.ho.proving.income.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;
import uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryCheckSerializeTest {

    private ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0, 0, 0, 0, 0).createObjectMapper();

    @Test
    public void thatJsonIsDeserialized() throws IOException {
        String json = "{\"category\": \"category-value\", \"calculationType\": \"calculationType-value\", \"passed\": \"false\", \"applicationRaisedDate\": \"2017-12-31\", \"assessmentStartDate\": \"2016-12-31\", \"failureReason\": \"NON_CONSECUTIVE_MONTHS\", \"threshold\": \"1560.50\", \"individuals\": []}";
        CategoryCheck categoryCheck = objectMapper.readValue(json, CategoryCheck.class);

        assertThat(categoryCheck).isNotNull();
        assertThat(categoryCheck.category()).isEqualTo("category-value");
        assertThat(categoryCheck.passed()).isEqualTo(false);
        assertThat(categoryCheck.applicationRaisedDate()).isEqualTo(LocalDate.of(2017, Month.DECEMBER, 31));
        assertThat(categoryCheck.assessmentStartDate()).isEqualTo(LocalDate.of(2016, Month.DECEMBER, 31));
        assertThat(categoryCheck.failureReason()).isEqualTo(IncomeValidationStatus.NON_CONSECUTIVE_MONTHS);
        assertThat(categoryCheck.threshold()).isEqualTo(new BigDecimal("1560.50"));
        assertThat(categoryCheck.individuals()).isNotNull();
        assertThat(categoryCheck.individuals().size()).isEqualTo(0);
    }

    @Test
    public void thatObjectIsSerialized() throws IOException {
        CategoryCheck categoryCheck = new CategoryCheck("category-value", "calculationType-value", false, LocalDate.of(2017, Month.DECEMBER, 31), LocalDate.of(2016, Month.DECEMBER, 31), IncomeValidationStatus.NON_CONSECUTIVE_MONTHS, new BigDecimal("1560.50"), new ArrayList<>());

        String json = objectMapper.writeValueAsString(categoryCheck);

        assertThat(json).isNotNull();
        assertThat(objectMapper.readTree(json)).isEqualTo(objectMapper.readTree("{\"category\": \"category-value\", \"calculationType\": \"calculationType-value\", \"passed\": false, \"applicationRaisedDate\": \"2017-12-31\", \"assessmentStartDate\": \"2016-12-31\", \"failureReason\": \"NON_CONSECUTIVE_MONTHS\", \"threshold\": \"1560.50\", \"individuals\": []}"));
    }
}
