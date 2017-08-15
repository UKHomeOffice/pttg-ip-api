package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.audit.AuditRepository;
import uk.gov.digital.ho.proving.income.domain.Income;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.digital.ho.proving.income.api.NinoUtils.sanitiseNino;
import static uk.gov.digital.ho.proving.income.api.NinoUtils.validateNino;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_RESPONSE;

@RestController
@Slf4j
public class IncomeRetrievalService {

    private final IncomeRecordService incomeRecordService;
    private final AuditRepository auditRepository;

    public IncomeRetrievalService(IncomeRecordService incomeRecordService, AuditRepository auditRepository) {
        this.incomeRecordService = incomeRecordService;
        this.auditRepository = auditRepository;
    }

    @GetMapping(value = "/incomeproving/v2/individual/{nino}/income", produces = APPLICATION_JSON_VALUE)
    public IncomeRetrievalResponse getIncome(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "forename") String forename,
        @RequestParam(value = "surname") String surname,
        @RequestParam(value = "dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
        @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Get income details of nino {} between {} and {}", nino, fromDate, toDate);

        UUID eventId = UUID.randomUUID();

        auditRepository.add(INCOME_PROVING_INCOME_CHECK_REQUEST, eventId, auditData(nino, forename, surname, dateOfBirth, fromDate, toDate));

        String cleanNino = sanitiseNino(nino);
        validateNino(cleanNino);

        if (fromDate == null) {
            throw new IllegalArgumentException("Parameter error: From date is invalid");
        } else if(fromDate.isAfter(now())){
            throw new IllegalArgumentException("Parameter error: fromDate");
        }


        if (toDate == null) {
            throw new IllegalArgumentException("Parameter error: To date is invalid");
        } else if(toDate.isAfter(now())){
            throw new IllegalArgumentException("Parameter error: toDate");
        }

        IncomeRecord incomeRecord = incomeRecordService.getIncomeRecord(
            new Identity(forename, surname, dateOfBirth, sanitiseNino(nino)),
            fromDate,
            toDate);


        IncomeRetrievalResponse incomeRetrievalResponse = new IncomeRetrievalResponse(
            new Individual("", forename, surname, sanitiseNino(nino)),
            incomeRecord.getIncome().
                stream().
                map(
                    income -> new Income(income.getPaymentDate(), getEmployer(income.getEmployerPayeReference(), incomeRecord.getEmployments()), income.getPayment().toString())
                ).
                filter( income ->
                    !(income.getPayDate().isBefore(fromDate)) && !(income.getPayDate().isAfter(toDate))
                ).
                collect(Collectors.toList())
        );

        log.info("Income check result: {}", value("incomeCheckResponse", incomeRetrievalResponse));

        auditRepository.add(INCOME_PROVING_INCOME_CHECK_RESPONSE, eventId, auditData(incomeRetrievalResponse));

        return incomeRetrievalResponse;
    }

    private String getEmployer(String employerPayeReference, List<Employments> employments) {
        return employments.
            stream().
            filter(employment -> employment.getEmployer().getPayeReference().equals(employerPayeReference)).
            findFirst().
            map(foundEmployment -> foundEmployment.getEmployer().getName()).
            orElse("unknown");
    }

    private Map<String, Object> auditData(String nino, String forename, String surname, LocalDate dateOfBirth, LocalDate fromDate, LocalDate toDate) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-income");
        auditData.put("nino", nino);
        auditData.put("forename", forename);
        auditData.put("surname", surname);
        auditData.put("dateOfBirth", format(dateOfBirth));
        auditData.put("fromDate", format(fromDate));
        auditData.put("toDate", format(toDate));

        return auditData;
    }

    private String format(LocalDate dateOfBirth) {
        return dateOfBirth == null ? null : dateOfBirth.format(DateTimeFormatter.ISO_DATE);
    }

    private Map<String, Object> auditData(IncomeRetrievalResponse response) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "get-income");
        auditData.put("response", response);

        return auditData;
    }
}
