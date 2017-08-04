package uk.gov.digital.ho.proving.income.audit

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import spock.lang.Specification

import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST

/**
 * @Author Home Office Digital
 */
class AuditActionsSpec extends Specification {

    private UUID generateEventId() {
        UUID.randomUUID()
    }

    def 'puts eventId in the data' (){

        when:
        def eventId = generateEventId()
        AuditApplicationEvent e = AuditActions.auditEvent(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, eventId, new HashMap<String, Object>())

        then:
        e.auditEvent.data.get("eventId") == eventId
    }

    def 'creates data map if it is null' (){

        when:
        def eventId = generateEventId()
        AuditApplicationEvent e = AuditActions.auditEvent(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, eventId, null)

        then:
        e.auditEvent.data != null
    }

    def 'generates event ids' (){
        expect:
        generateEventId() != generateEventId()
    }
}
