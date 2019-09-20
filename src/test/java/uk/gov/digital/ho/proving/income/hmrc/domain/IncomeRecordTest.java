package uk.gov.digital.ho.proving.income.hmrc.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.proving.income.application.ServiceConfiguration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomeRecordTest {

    private Income a = new Income(new BigDecimal("1"),
                                    LocalDate.MAX,
                                    1,
                                    1,
                                    "a");

    private Income b = new Income(new BigDecimal("2"),
                                    LocalDate.now(),
                                    1,
                                    1,
                                    "b");

    @Test
    public void shouldNotRemoveItems() {

        List<Income> incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(b);

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null, null, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.paye().equals(deDuplicated)).isTrue();
    }

    @Test
    public void shouldRemoveItems() {

        List<Income> incomeWithoutDuplicates = new ArrayList<>();

        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(a);
        incomeWithoutDuplicates.add(b);
        incomeWithoutDuplicates.add(b);

        IncomeRecord incomeRecord = new IncomeRecord(incomeWithoutDuplicates, null, null, null);

        List<Income> deDuplicated = incomeRecord.deDuplicatedIncome();

        assertThat(incomeRecord.paye().equals(deDuplicated)).isFalse();
        assertThat(incomeRecord.paye().size()).isEqualTo(4);
        assertThat(deDuplicated.size()).isEqualTo(2);
        assertThat(incomeRecord.paye().containsAll(deDuplicated));
    }

    @Test
    public void shouldDeserializeJson() throws IOException {
        ObjectMapper objectMapper = new ServiceConfiguration("", 0, 0, 0, 0, 0, 0).createObjectMapper();

        String json = "{\"paye\":[],\"selfAssessment\":[],\"employments\":[],\"individual\":{\"firstName\": \"firstname\", \"lastName\": \"lastname\", \"dateOfBirth\": \"1970-01-01\", \"nino\": \"QQ123456C\"}}";

        IncomeRecord incomeRecord = objectMapper.readValue(json, IncomeRecord.class);

        assertThat(incomeRecord).isNotNull();
        assertThat(incomeRecord.individual().nino()).isEqualTo("QQ123456C");
        assertThat(incomeRecord.individual().dateOfBirth()).isEqualTo(LocalDate.parse("1970-01-01"));
        assertThat(incomeRecord.individual().lastName()).isEqualTo("lastname");
        assertThat(incomeRecord.individual().firstName()).isEqualTo("firstname");
    }
}
