package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class IncomeRecordServiceNotProductionResponseLogger implements ServiceResponseLogger {

    private final ObjectMapper mapper;

    public IncomeRecordServiceNotProductionResponseLogger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void record(Identity identity, IncomeRecord incomeRecord) {
        try {
            log.info(mapper.writeValueAsString(produceLogEntry(identity, incomeRecord)));
        } catch (JsonProcessingException e) {
            log.error("Failed to turn IncomeRecord response data into JSON");
        }
    }

    public Map<String, Object> produceLogEntry(Identity identity, IncomeRecord incomeRecord) {
        Map<String, Object> logEntry = new LinkedHashMap<>();

        logEntry.put("identity", identity);
        logEntry.put("incomeRecord", incomeRecord);

        return logEntry;
    }
}
