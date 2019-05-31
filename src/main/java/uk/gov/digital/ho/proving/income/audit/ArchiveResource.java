package uk.gov.digital.ho.proving.income.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.income.api.RequestData;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.proving.income.api.RequestData.REQUEST_DURATION_MS;

@RestController
@Slf4j
public class ArchiveResource {

    private AuditArchiveService auditArchiveService;
    private RequestData requestData;

    public ArchiveResource(
        AuditArchiveService auditArchiveService,
        RequestData requestData
    ) {
        this.auditArchiveService = auditArchiveService;
        this.requestData = requestData;
    }

    @PostMapping(value = "/archive")
    public void archive() {
        log.info("Request received on /archive");

        // TODO Remove this - it's just for a test
        try {
            Thread.sleep(61000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        auditArchiveService.archiveAudit();

        log.info("OK response returned for /archive",
            value(REQUEST_DURATION_MS, requestData.calculateRequestDuration()));
    }
}
