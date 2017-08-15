package uk.gov.digital.ho.proving.income.api.test

import groovy.json.JsonSlurper
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.ApiExceptionHandler
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch
import uk.gov.digital.ho.proving.income.api.FinancialStatusService
import uk.gov.digital.ho.proving.income.audit.AuditRepository
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecord
import uk.gov.digital.ho.proving.income.domain.hmrc.IncomeRecordService

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.api.FinancialCheckValues.MONTHLY_VALUE_BELOW_THRESHOLD
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getConsecutiveIncomes2
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getEmployments
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE

class FinancialServiceSpec extends Specification {


    def mockIncomeRecordService = Mock(IncomeRecordService)
    def mockAuditRepository = Mock(AuditRepository)

    def financialStatusController = new FinancialStatusService(mockIncomeRecordService, mockAuditRepository)

    MockMvc mockMvc = standaloneSetup(financialStatusController).setControllerAdvice(new ApiExceptionHandler()).build()


    def "valid NINO is looked up on the earnings service"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())

        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/financialstatus").
            param("applicationRaisedDate", "2015-09-23").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }


    def "invalid nino is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/CHICKENS/financialstatus").
            param("applicationRaisedDate", "2015-09-23").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )


        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Invalid NINO"

    }

    def "unknown nino yields HTTP Not Found (404)"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> { throw new EarningsServiceNoUniqueMatch() }


        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456C/financialstatus").
            param("applicationRaisedDate", "2015-09-23").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isNotFound())
        jsonContent.status.message == "Resource not found"

    }

    def "cannot submit less than zero dependants"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456C/financialstatus").
                param("applicationRaisedDate", "2015-09-23").
                param("dependants", "-1").
                param("forename", "Mark").
                param("surname", "Jones").
                param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Dependants cannot be less than 0"
    }

    def "can submit more than zero dependants"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())

        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456C/financialstatus").
            param("applicationRaisedDate", "2015-09-23").
            param("dependants", "1").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }

    def "invalid date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456C/financialstatus").
            param("applicationRaisedDate", "2015-03-XX").
            param("dependants", "1").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Invalid value for applicationRaisedDate"
    }

    def "future date is rejected"() {
        given:
        String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456C/financialstatus").
            param("applicationRaisedDate", tomorrow).
            param("dependants", "1").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: applicationRaisedDate"
    }

    def "monthly payment uses 182 days in start date calculation"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())

        when:
        def response = mockMvc.perform(get("/incomeproving/v2/individual/AA123456A/financialstatus").
            param("applicationRaisedDate", "2015-09-23").
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.categoryCheck.assessmentStartDate == "2015-03-25"

    }

    def 'audits search inputs and response'() {

        given:
        def nino = 'AA123456A'
        def applicationRaisedDate = "2015-09-23"
        def dependants = "1"
        def category = 'A'

        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> new IncomeRecord(getConsecutiveIncomes2(), getEmployments())

        String requestType
        String requestEventId
        Map<String, Object> requestEvent

        String responseType
        String responseEventId
        Map<String, Object> responseEvent

        1 * mockAuditRepository.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, _, _) >> { args -> requestType = args[0]; requestEventId = args[1]; requestEvent = args[2]}
        1 * mockAuditRepository.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, _, _) >> { args -> responseType = args[0]; responseEventId = args[1]; responseEvent = args[2]}

        when:
        mockMvc.perform(get("/incomeproving/v2/individual/$nino/financialstatus").
            param("applicationRaisedDate", applicationRaisedDate).
            param("dependants", dependants).
            param("forename", "Mark").
            param("surname", "Jones").
            param("dateOfBirth", "1980-01-13")
        )

        then:

        requestEventId == responseEventId

        requestType == INCOME_PROVING_FINANCIAL_STATUS_REQUEST.name()
        requestEvent['nino'] == nino
        requestEvent['forename'] == "Mark"
        requestEvent['surname'] == "Jones"
        requestEvent['dateOfBirth'] == "1980-01-13"
        requestEvent['applicationRaisedDate'] == applicationRaisedDate
        requestEvent['dependants'] == Integer.parseInt(dependants)
        requestEvent['method'] == "get-financial-status"

        responseType == INCOME_PROVING_FINANCIAL_STATUS_RESPONSE.name()
        responseEvent['method'] == "get-financial-status"
        responseEvent['response'].individual.title == ""
        responseEvent['response'].individual.forename == "Mark"
        responseEvent['response'].individual.surname == "Jones"
        responseEvent['response'].individual.nino == nino
        responseEvent['response'].categoryCheck.category == category
        responseEvent['response'].categoryCheck.passed == false
        responseEvent['response'].categoryCheck.applicationRaisedDate == "2015-09-23"
        responseEvent['response'].categoryCheck.assessmentStartDate == "2015-03-25"
        responseEvent['response'].categoryCheck.failureReason == MONTHLY_VALUE_BELOW_THRESHOLD
        responseEvent['response'].categoryCheck.threshold == 1866.67
        responseEvent['response'].categoryCheck.employers.size == 2
        responseEvent['response'].categoryCheck.employers[0] == "Pizza Hut"
        responseEvent['response'].categoryCheck.employers[1] == "Burger King"
        responseEvent['response'].status.code == "100"
        responseEvent['response'].status.message == "OK"
    }

}
