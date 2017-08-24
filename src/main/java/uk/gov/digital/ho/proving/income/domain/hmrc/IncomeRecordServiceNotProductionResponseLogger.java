package uk.gov.digital.ho.proving.income.domain.hmrc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncomeRecordServiceNotProductionResponseLogger implements ServiceResponseLogger {

    private final ObjectMapper mapper;

    public IncomeRecordServiceNotProductionResponseLogger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void record(IncomeRecord responseEntity) {
        try {
            log.info(mapper.writeValueAsString(responseEntity));
        } catch (JsonProcessingException e) {
            log.error("Failed to turn IncomeRecord response data into JSON");
        }
    }
}
