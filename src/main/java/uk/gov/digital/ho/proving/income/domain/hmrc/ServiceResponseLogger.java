package uk.gov.digital.ho.proving.income.domain.hmrc;

public interface ServiceResponseLogger {
    void record(IncomeRecord responseEntity);
}
