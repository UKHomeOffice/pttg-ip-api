package uk.gov.digital.ho.proving.income.hmrc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.proving.income.application.LogEvent.EVENT;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_JSON_FAILURE;
import static uk.gov.digital.ho.proving.income.application.LogEvent.INCOME_PROVING_SERVICE_MAPPED_TO_JSON;

@Slf4j
public class IncomeRecordServiceNotProductionResponseLogger implements ServiceResponseLogger {

    private final ObjectMapper mapper;

    public IncomeRecordServiceNotProductionResponseLogger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void record(Identity identity, IncomeRecord incomeRecord) {
        try {
            log.info(mapper.writeValueAsString(produceLogEntry(identity, incomeRecord)), value(EVENT, INCOME_PROVING_SERVICE_MAPPED_TO_JSON));
        } catch (JsonProcessingException e) {
            log.error("Failed to turn IncomeRecord response data into JSON", value(EVENT, INCOME_PROVING_SERVICE_JSON_FAILURE));
        }
    }

    Map<String, Object> produceLogEntry(Identity identity, IncomeRecord incomeRecord) {
        Map<String, Object> logEntry = new LinkedHashMap<>();

        logEntry.put("identity", identity);
        logEntry.put("incomeRecord", incomeRecord);

        return logEntry;
    }
}
