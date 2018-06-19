package uk.gov.digital.ho.proving.income.api.test

import groovy.json.JsonSlurper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.api.FinancialStatusService
import uk.gov.digital.ho.proving.income.api.NinoUtils
import uk.gov.digital.ho.proving.income.application.ApplicationExceptions
import uk.gov.digital.ho.proving.income.application.ResourceExceptionHandler
import uk.gov.digital.ho.proving.income.audit.AuditClient
import uk.gov.digital.ho.proving.income.hmrc.domain.AnnualSelfAssessmentTaxReturn
import uk.gov.digital.ho.proving.income.hmrc.HmrcClient

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.validator.domain.IncomeValidationStatus.MONTHLY_VALUE_BELOW_THRESHOLD
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_REQUEST
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.INCOME_PROVING_FINANCIAL_STATUS_RESPONSE
import static uk.gov.digital.ho.proving.income.validator.TestData.getConsecutiveIncomes2;

class FinancialServiceSpec extends Specification {


    def mockIncomeRecordService = Mock(HmrcClient)
    def mockAuditClient = Mock(AuditClient)
    def mockNinoUtils = Mock(NinoUtils)

    def financialStatusController = new FinancialStatusService(mockIncomeRecordService, mockAuditClient, mockNinoUtils)

    def emptyTaxes = new ArrayList<AnnualSelfAssessmentTaxReturn>()

    MockMvc mockMvc = standaloneSetup(financialStatusController).setControllerAdvice(new ResourceExceptionHandler(mockAuditClient)).build()


    def "valid NINO is looked up on the earnings service 2"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }

    def "invalid nino is rejected"() {
        given:
        mockNinoUtils.sanitise("AA12345") >> "AA12345"
        mockNinoUtils.validate("AA12345") >> { throw new IllegalArgumentException("Error: Invalid NINO") }

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA12345\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}")
        )


        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Error: Invalid NINO"

    }


    def "unknown nino yields HTTP Not Found (404)"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> { throw new ApplicationExceptions.EarningsServiceNoUniqueMatchException() }


        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isNotFound())
        jsonContent.status.message == "Resource not found"

    }

    def "cannot submit less than zero dependants"() {
        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":-1}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Error: Dependants cannot be less than 0"
    }

    def "can submit more than zero dependants"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord()

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":1}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }

    def "invalid date is rejected"() {
        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2017-08-21\"}],\"applicationRaisedDate\":\"2017-08-nm\",\"dependants\":0}")
        )

        then:
        //def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
    }

    def "future date is rejected"() {
        given:
        String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"2000-08-21\"}],\"applicationRaisedDate\":\"2028-08-21\",\"dependants\":0}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Error: applicationRaisedDate"
    }

    def "monthly payment uses 182 days in start date calculation"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord()

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Mark\",\"surname\":\"Jones\",\"dateOfBirth\":\"1980-01-13\"}],\"applicationRaisedDate\":\"2015-09-23\",\"dependants\":0}")
        )

        then:

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.categoryChecks[0].assessmentStartDate == "2015-03-25"

    }

    def 'audits search inputs and response'() {

        given:
        def nino = 'AA123456A'
        def applicationRaisedDate = "2015-09-23"
        def dependants = "1"
        def category = 'A'

        mockNinoUtils.sanitise(nino) >> nino

        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord()

        String requestType
        String requestEventId
        Map<String, Object> requestEvent

        String responseType
        String responseEventId
        Map<String, Object> responseEvent

        1 * mockAuditClient.add(INCOME_PROVING_FINANCIAL_STATUS_REQUEST, _, _) >> { args -> requestType = args[0]; requestEventId = args[1]; requestEvent = args[2]}
        1 * mockAuditClient.add(INCOME_PROVING_FINANCIAL_STATUS_RESPONSE, _, _) >> { args -> responseType = args[0]; responseEventId = args[1]; responseEvent = args[2]}

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Duncan\",\"surname\":\"Smith\",\"dateOfBirth\":\"1970-01-01\"}],\"applicationRaisedDate\":\"2015-09-23\",\"dependants\":1}")
        )

        then:

        requestEventId == responseEventId

        requestType == INCOME_PROVING_FINANCIAL_STATUS_REQUEST.name()
        requestEvent['nino'] == nino
        requestEvent['forename'] == "Duncan"
        requestEvent['surname'] == "Smith"
        requestEvent['dateOfBirth'] == "1970-01-01"
        requestEvent['applicationRaisedDate'] == applicationRaisedDate
        requestEvent['dependants'] == Integer.parseInt(dependants)
        requestEvent['method'] == "get-financial-status"

        responseType == INCOME_PROVING_FINANCIAL_STATUS_RESPONSE.name()
        responseEvent['method'] == "get-financial-status"
        responseEvent['response'].individuals[0].title == ""
        responseEvent['response'].individuals[0].forename == "Duncan"
        responseEvent['response'].individuals[0].surname == "Smith"
        responseEvent['response'].individuals[0].nino == nino
        responseEvent['response'].categoryChecks[0].category == category
        responseEvent['response'].categoryChecks[0].passed == false
        responseEvent['response'].categoryChecks[0].applicationRaisedDate == "2015-09-23"
        responseEvent['response'].categoryChecks[0].assessmentStartDate == "2015-03-25"
        responseEvent['response'].categoryChecks[0].failureReason == MONTHLY_VALUE_BELOW_THRESHOLD
        responseEvent['response'].categoryChecks[0].threshold == 1866.67
        responseEvent['response'].categoryChecks[0].individuals[0].employers.size == 2
        responseEvent['response'].categoryChecks[0].individuals[0].employers[0] == "Pizza Hut"
        responseEvent['response'].categoryChecks[0].individuals[0].employers[1] == "Burger King"
        responseEvent['response'].status().code == "100"
        responseEvent['response'].status().message == "OK"
    }

    def "individual details from HMRC are returned when present"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord()

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Duncan\",\"surname\":\"Smith\",\"dateOfBirth\":\"1970-01-01\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.individuals[0].forename == "Duncan"
        jsonContent.individuals[0].surname == "Smith"
    }

    def "individual details from request are returned when HMRC individual not present"() {
        given:
        1 * mockIncomeRecordService.getIncomeRecord(_, _, _) >> getConsecutiveIncomes2().get(0).incomeRecord()

        when:
        def response = mockMvc.perform(post("/incomeproving/v3/individual/financialstatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"individuals\": [{\"nino\":\"AA123456A\",\"forename\":\"Duncan\",\"surname\":\"Smith\",\"dateOfBirth\":\"1970-01-01\"}],\"applicationRaisedDate\":\"2017-08-21\",\"dependants\":0}")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.individuals[0].forename == "Duncan"
        jsonContent.individuals[0].surname == "Smith"
    }

}
