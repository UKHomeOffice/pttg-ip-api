package uk.gov.digital.ho.proving.income.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
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
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;

@RestController
@ControllerAdvice
public class IncomeRetrievalV2Service{
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final IncomeRecordService incomeRecordService;

    private final ApplicationEventPublisher auditor;

    public IncomeRetrievalV2Service(IncomeRecordService incomeRecordService, ApplicationEventPublisher auditor) {
        this.incomeRecordService = incomeRecordService;
        this.auditor = auditor;
    }

    @RequestMapping(value = "/incomeproving/v2/individual/{nino}/income", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public IncomeRetrievalResponse getIncome(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "forename") String forename,
        @RequestParam(value = "surname") String surname,
        @RequestParam(value = "dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
        @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LOGGER.debug("Get income details invoked for {} nino between {} and {}", value("nino", nino), fromDate, toDate);

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(nino, forename, surname, dateOfBirth, fromDate, toDate)));

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


        IncomeRetrievalResponse incomeRetrievalResponse = new IncomeRetrievalResponse();
        incomeRetrievalResponse.setIndividual(new Individual("", forename, surname, sanitiseNino(nino)));
        incomeRetrievalResponse.setIncomes(incomeRecord.getIncome().
            stream().
            map(
                income -> new Income(income.getPaymentDate(), getEmployer(income.getEmployerPayeReference(), incomeRecord.getEmployments()), income.getPayment().toString())
            ).
            filter( income ->
                !(income.getPayDate().isBefore(fromDate)) && !(income.getPayDate().isAfter(toDate))
            ).
            collect(Collectors.toList()));

        LOGGER.debug("Income check result: {}", value("incomeCheckResponse", incomeRetrievalResponse));
        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(incomeRetrievalResponse)));

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
