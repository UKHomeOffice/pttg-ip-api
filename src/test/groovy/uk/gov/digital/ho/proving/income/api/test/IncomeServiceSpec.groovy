package uk.gov.digital.ho.proving.income.api.test

import groovy.json.JsonSlurper
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.ApiExceptionHandler
import uk.gov.digital.ho.proving.income.api.IncomeRetrievalService
import uk.gov.digital.ho.proving.income.audit.AuditRepository
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.*
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_REQUEST
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_INCOME_CHECK_RESPONSE

/**
 * @Author Home Office Digital
 */

class IncomeServiceSpec extends Specification {

    String yesterday = now().minusDays(1).format(ISO_LOCAL_DATE);
    String today = now().format(ISO_LOCAL_DATE);
    String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

    def mockIncomeRecordService = Mock(IncomeRecordService)
    def mockAuditRepository = Mock(AuditRepository)

    def controller = new IncomeRetrievalService(mockIncomeRecordService, mockAuditRepository)

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

        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())


        String requestType
        String requestEventId
        Map<String, Object> requestEvent

        String responseType
        String responseEventId
        Map<String, Object> responseEvent

        1 * mockAuditRepository.add(INCOME_PROVING_INCOME_CHECK_REQUEST, _, _) >> { args -> requestType = args[0]; requestEventId = args[1]; requestEvent = args[2]}
        1 * mockAuditRepository.add(INCOME_PROVING_INCOME_CHECK_RESPONSE, _, _) >> { args -> responseType = args[0]; responseEventId = args[1]; responseEvent = args[2]}

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

        requestEventId == responseEventId

        requestType == INCOME_PROVING_INCOME_CHECK_REQUEST.name()
        requestEvent['method'] == "get-income"
        requestEvent['nino'] == nino
        requestEvent['forename'] == "Mark"
        requestEvent['surname'] == "Jones"
        requestEvent['dateOfBirth'] == "1980-01-13"
        requestEvent['fromDate'] == yesterday
        requestEvent['toDate'] == today

        responseType == INCOME_PROVING_INCOME_CHECK_RESPONSE.name()
        responseEvent['method'] == "get-income"
        responseEvent['response'].individual.title == ""
        responseEvent['response'].individual.forename == "Mark"
        responseEvent['response'].individual.surname == "Jones"
        responseEvent['response'].individual.forename == "Mark"
        responseEvent['response'].incomes.size == 0
        responseEvent['response'].total == "0"
    }
}
