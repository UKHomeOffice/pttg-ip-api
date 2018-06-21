package uk.gov.digital.ho.proving.income.hmrc;

import uk.gov.digital.ho.proving.income.hmrc.domain.Identity;
import uk.gov.digital.ho.proving.income.hmrc.domain.IncomeRecord;

public interface ServiceResponseLogger {
    void record(Identity identity, IncomeRecord responseEntity);
}
