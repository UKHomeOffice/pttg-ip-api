package uk.gov.digital.ho.proving.income.api.test

import groovy.json.JsonSlurper
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.ApiExceptionHandler
import uk.gov.digital.ho.proving.income.api.IncomeRetrievalV2Service
import uk.gov.digital.ho.proving.income.audit.AuditEventType
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getConsecutiveIncomes2
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getEmployments
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getIndividual

/**
 * @Author Home Office Digital
 */

class IncomeService2Spec extends Specification {

    String yesterday = now().minusDays(1).format(ISO_LOCAL_DATE);
    String today = now().format(ISO_LOCAL_DATE);
    String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

    def incomeRecordService = Mock(IncomeRecordService)
    ApplicationEventPublisher auditor = Mock()
    def controller = new IncomeRetrievalV2Service(incomeRecordService, auditor)

    MockMvc mockMvc = standaloneSetup(controller).setControllerAdvice(new ApiExceptionHandler()).build()


    def "invalid from date is rejected"() {

        when:
        def response = mockMvc.perform(
            get("/incomeproving/v2/individual/AA123456A/income")
                .param("fromDate","2016-03-xx")
                .param("toDate", yesterday)
                .param("forename", "Mark")
                .param("surname", "Jones")
                .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Invalid value for fromDate"

    }

    def "invalid from and to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/income")
            .param("fromDate","2016-03-xx")
            .param("toDate", "2016-03-xx")
            .param("forename", "Mark")
            .param("surname", "Jones")
            .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Invalid value for fromDate"
    }

    def "invalid to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/income")
            .param("fromDate",yesterday)
            .param("toDate", "2016-03-xx")
            .param("forename", "Mark")
            .param("surname", "Jones")
            .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Invalid value for toDate"
    }

    def "future from date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/income")
            .param("fromDate",tomorrow)
            .param("toDate", yesterday)
            .param("forename", "Mark")
            .param("surname", "Jones")
            .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: fromDate"
    }

    def "future to date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/income")
            .param("fromDate",yesterday)
            .param("toDate", tomorrow)
            .param("forename", "Mark")
            .param("surname", "Jones")
            .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: toDate"
    }

    def "future from and to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/income")
            .param("fromDate",tomorrow)
            .param("toDate", tomorrow)
            .param("forename", "Mark")
            .param("surname", "Jones")
            .param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: fromDate"
    }

    def 'audits search inputs and response'() {

        given:
        def nino = 'AA123456A'
        def total = "9600"
        def frequency = "M1"
        def individual = getIndividual()

        1 * incomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())

        AuditEvent event1
        AuditEvent event2
        1 * auditor.publishEvent(_) >> {args -> event1 = args[0].auditEvent}
        1 * auditor.publishEvent(_) >> {args -> event2 = args[0].auditEvent}

        when:
        mockMvc.perform(
            get("/incomeproving/v2/individual/$nino/income")
                .param("fromDate", yesterday)
                .param("toDate", today)
                .param("forename", "Mark")
                .param("surname", "Jones")
                .param("dateOfBirth", "1980-01-13")
        )

        then:

        event1.type == AuditEventType.SEARCH.name()
        event2.type == AuditEventType.SEARCH_RESULT.name()

        event1.data['eventId'] == event2.data['eventId']

        event1.data['nino'] == nino
        event1.data['fromDate'] == yesterday
        event1.data['toDate'] == today

        event2.data['response'].individual.forename == "Mark"
    }
}
