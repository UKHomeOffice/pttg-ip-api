package uk.gov.digital.ho.proving.income.domain.hmrc;

public class IncomeRecordServiceProductionResponseLogger implements ServiceResponseLogger {
    @Override
    public void record(Identity identity, IncomeRecord responseEntity) {
        // Anything logged here will be consumed by Elastic Search in the Production Environment, so is effectively persisted - Caveat Lector!
    }
}
