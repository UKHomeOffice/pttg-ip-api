package uk.gov.digital.ho.proving.income.api.test

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.ServiceConfiguration
import uk.gov.digital.ho.proving.income.acl.MongodbBackedApplicantService
import uk.gov.digital.ho.proving.income.acl.MongodbBackedEarningsService
import uk.gov.digital.ho.proving.income.api.IncomeRetrievalService
import uk.gov.digital.ho.proving.income.audit.AuditEventType
import uk.gov.digital.ho.proving.income.domain.Income
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse
import uk.gov.digital.ho.proving.income.domain.Individual

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static java.util.Arrays.asList
import static org.hamcrest.core.StringContains.containsString
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getConsecutiveIncomes
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getIndividual

/**
 * @Author Home Office Digital
 */

class IncomeServiceSpec extends Specification {

    String yesterday = now().minusDays(1).format(ISO_LOCAL_DATE);
    String today = now().format(ISO_LOCAL_DATE);
    String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

    def incomeRetrievalService = new IncomeRetrievalService()
    def earningsService = Mock(MongodbBackedEarningsService)
    def individualService = Mock(MongodbBackedApplicantService)

    MockMvc mockMvc = standaloneSetup(incomeRetrievalService).build()

    ApplicationEventPublisher auditor = Mock()

    def setup() {
        incomeRetrievalService.setEarningsService(earningsService)
        incomeRetrievalService.setIndividualService(individualService)

        incomeRetrievalService.auditor = auditor
    }




    def "invalid from date is rejected"() {

        // 1 * earningsService.lookup(_,_) >> new TemporaryMigrationFamilyApplication(getIndividual(), new Date(), "A", true)
        // 1 * individualService.lookup(_,_,_) >> new IncomeProvingResponse(getIndividual(), getConsecutiveIncomes(), "9600", "M1")

        when:
        def response = mockMvc.perform(
            get("/incomeproving/v1/individual/AA123456A/income")
                .param("fromDate","2016-03-xx")
                .param("toDate", yesterday)
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: From date is invalid"

    }

    def "invalid from and to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/income")
            .param("fromDate","2016-03-xx")
            .param("toDate", "2016-03-xx")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: From date is invalid"
    }

    def "invalid to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/income")
            .param("fromDate",yesterday)
            .param("toDate", "2016-03-xx")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: To date is invalid"
    }

    def "future from date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/income")
            .param("fromDate",tomorrow)
            .param("toDate", yesterday)
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: fromDate"
    }

    def "future to date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/income")
            .param("fromDate",yesterday)
            .param("toDate", tomorrow)
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: toDate"
    }

    def "future from and to dates are rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/income")
            .param("fromDate",tomorrow)
            .param("toDate", tomorrow)
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

        individualService.lookup(_,_,_) >> new IncomeProvingResponse(individual, getConsecutiveIncomes(), total, frequency)

        AuditEvent event1
        AuditEvent event2
        1 * auditor.publishEvent(_) >> {args -> event1 = args[0].auditEvent}
        1 * auditor.publishEvent(_) >> {args -> event2 = args[0].auditEvent}

        when:
        mockMvc.perform(
            get("/incomeproving/v1/individual/$nino/income")
                .param("fromDate", yesterday)
                .param("toDate", today)
        )

        then:

        event1.type == AuditEventType.SEARCH.name()
        event2.type == AuditEventType.SEARCH_RESULT.name()

        event1.data['eventId'] == event2.data['eventId']

        event1.data['nino'] == nino
        event1.data['fromDate'] == yesterday
        event1.data['toDate'] == today

        event2.data['response'].individual.forename == individual.forename
    }
}
