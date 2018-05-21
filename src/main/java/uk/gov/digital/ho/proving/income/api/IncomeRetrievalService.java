package uk.gov.digital.ho.proving.income.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.income.audit.AuditClient;
import uk.gov.digital.ho.proving.income.domain.Income;
import uk.gov.digital.ho.proving.income.domain.Individual;
import uk.gov.digital.ho.proving.income.domain.hmrc.Employments;
import uk.gov.digital.ho.proving.income.domain.hmrc.HmrcClient;
import uk.gov.digital.ho.proving.income.domain.hmrc.Identity;
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord;

import javax.validation.Valid;
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
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_REQUEST;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_RESPONSE;

@RestController
@Slf4j
public class IncomeRetrievalService {

    private final HmrcClient hmrcClient;
    private final AuditClient auditClient;
    private final NinoUtils ninoUtils;

    public IncomeRetrievalService(HmrcClient hmrcClient, AuditClient auditClient, NinoUtils ninoUtils) {
        this.hmrcClient = hmrcClient;
        this.auditClient = auditClient;
        this.ninoUtils = ninoUtils;
    }

    @Deprecated
    @GetMapping(value = "/incomeproving/v2/individual/{nino}/income", produces = APPLICATION_JSON_VALUE)
    public IncomeRetrievalResponse getIncomeDeprecated(
        @PathVariable(value = "nino") String nino,
        @RequestParam(value = "forename") String forename,
        @RequestParam(value = "surname") String surname,
        @RequestParam(value = "dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
        @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        final String redactedNino = ninoUtils.redact(nino);
        log.info("Get income details of nino {} between {} and {}", redactedNino, fromDate, toDate);

        UUID eventId = UUID.randomUUID();

        auditClient.add(INCOME_PROVING_INCOME_CHECK_REQUEST, eventId, auditData(nino, forename, surname, dateOfBirth, fromDate, toDate));

        String sanitisedNino = ninoUtils.sanitise(nino);
        ninoUtils.validate(sanitisedNino);

        if (fromDate == null) {
            throw new IllegalArgumentException("Error: From date is invalid");
        } else if(fromDate.isAfter(now())){
            throw new IllegalArgumentException("Error: fromDate");
        }


        if (toDate == null) {
            throw new IllegalArgumentException("Error: To date is invalid");
        } else if(toDate.isAfter(now())){
            throw new IllegalArgumentException("Error: toDate");
        }

        IncomeRecord incomeRecord = hmrcClient.getIncomeRecord(
            new Identity(forename, surname, dateOfBirth, sanitisedNino),
            fromDate,
            toDate);


        IncomeRetrievalResponse incomeRetrievalResponse = new IncomeRetrievalResponse(
            new Individual(forename, surname, sanitisedNino),
            incomeRecord.getPaye().
                stream().
                map(
                    income -> new Income(income.getPaymentDate(), getEmployer(income.getEmployerPayeReference(), incomeRecord.getEmployments()), income.getPayment().toString())
                ).
                filter( income ->
                    !(income.getPayDate().isBefore(fromDate)) && !(income.getPayDate().isAfter(toDate))
                ).
                collect(Collectors.toList())
        );

        log.info("Income check result for: {}", value("nino", redactedNino));

        auditClient.add(INCOME_PROVING_INCOME_CHECK_RESPONSE, eventId, auditData(incomeRetrievalResponse));

        return incomeRetrievalResponse;
    }

    @PostMapping(value = "/incomeproving/v2/individual/income", produces = APPLICATION_JSON_VALUE)
    public IncomeRetrievalResponse getIncome(@Valid @RequestBody IncomeRetrievalRequest request) {

        final String redactedNino = ninoUtils.redact(request.getNino());
        log.info("Retrieve income details of nino {} between {} and {}", redactedNino, request.getFromDate(), request.getToDate());

        UUID eventId = UUID.randomUUID();

        auditClient.add(INCOME_PROVING_INCOME_CHECK_REQUEST, eventId, auditData(request.getNino(), request.getForename(), request.getSurname(), request.getDateOfBirth(), request.getFromDate(), request.getToDate()));

        String sanitisedNino = ninoUtils.sanitise(request.getNino());
        ninoUtils.validate(sanitisedNino);

        if (request.getFromDate() == null) {
            throw new IllegalArgumentException("Error: From date is invalid");
        } else if(request.getFromDate().isAfter(now())){
            throw new IllegalArgumentException("Error: fromDate");
        }


        if (request.getToDate() == null) {
            throw new IllegalArgumentException("Error: To date is invalid");
        } else if(request.getToDate().isAfter(now())){
            throw new IllegalArgumentException("Error: toDate");
        }

        IncomeRecord incomeRecord = hmrcClient.getIncomeRecord(
            new Identity(request.getForename(), request.getSurname(), request.getDateOfBirth(), sanitisedNino),
            request.getFromDate(),
            request.getToDate());


        IncomeRetrievalResponse incomeRetrievalResponse = new IncomeRetrievalResponse(
            new Individual(request.getForename(), request.getSurname(), sanitisedNino),
            incomeRecord.getPaye().
                stream().
                map(
                    income -> new Income(income.getPaymentDate(), getEmployer(income.getEmployerPayeReference(), incomeRecord.getEmployments()), income.getPayment().toString())
                ).
                filter( income ->
                    !(income.getPayDate().isBefore(request.getFromDate())) && !(income.getPayDate().isAfter(request.getToDate()))
                ).
                collect(Collectors.toList())
        );

        log.info("Income check result for: {}", value("nino", redactedNino));

        auditClient.add(INCOME_PROVING_INCOME_CHECK_RESPONSE, eventId, auditData(incomeRetrievalResponse));

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
