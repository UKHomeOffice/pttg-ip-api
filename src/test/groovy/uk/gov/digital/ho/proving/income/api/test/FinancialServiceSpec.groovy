import groovy.json.JsonSlurper
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.acl.EarningsServiceNoUniqueMatch
import uk.gov.digital.ho.proving.income.acl.MongodbBackedApplicantService
import uk.gov.digital.ho.proving.income.acl.MongodbBackedEarningsService
import uk.gov.digital.ho.proving.income.api.FinancialStatusService
import uk.gov.digital.ho.proving.income.domain.IncomeProvingResponse
import uk.gov.digital.ho.proving.income.domain.TemporaryMigrationFamilyApplication

import java.time.LocalDate

import static java.time.LocalDate.now
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getConsecutiveIncomes
import static uk.gov.digital.ho.proving.income.api.test.MockDataUtils.getIndividual

class FinancialServiceSpec extends Specification {


    def financialStatusController = new FinancialStatusService()
    def earningsService = Mock(MongodbBackedEarningsService)
    def individualService = Mock(MongodbBackedApplicantService)
    MockMvc mockMvc = standaloneSetup(financialStatusController).build()

    def setup() {
        financialStatusController.setEarningsService(earningsService)
        financialStatusController.setIndividualService(individualService)
    }

    def "valid NINO is looked up on the earnings service"() {
        given:
        1 * earningsService.lookup(_, _) >> new TemporaryMigrationFamilyApplication(getIndividual(), LocalDate.now(), "A", true)
        1 * individualService.lookup(_, _, _) >> new IncomeProvingResponse(getIndividual(), getConsecutiveIncomes(), "9600", "M1")

        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/financialstatus").param("applicationRaisedDate", "2015-09-23"))

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }


    def "invalid nino is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/financialstatus").param("applicationRaisedDate", "2015-09-23"))

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: NINO is invalid"

    }

    def "unknown nino yields HTTP Not Found (404)"() {
        given:
        1 * individualService.lookup(_, _, _) >> { throw new EarningsServiceNoUniqueMatch() }

        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456C/financialstatus").param("applicationRaisedDate", "2015-03-21"))

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isNotFound())
        jsonContent.status.message == "Resource not found"

    }

    def "cannot submit less than zero dependants"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456C/financialstatus")
            .param("applicationRaisedDate", "2015-03-21")
            .param("dependants", "-1")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Dependants cannot be less than 0"
    }

    def "can submit more than zero dependants"() {
        given:
        1 * earningsService.lookup(_, _) >> new TemporaryMigrationFamilyApplication(getIndividual(), LocalDate.now(), "A", true)
        1 * individualService.lookup(_, _, _) >> new IncomeProvingResponse(getIndividual(), getConsecutiveIncomes(), "9600", "M1")

        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456C/financialstatus")
            .param("applicationRaisedDate", "2015-03-21")
            .param("dependants", "1")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.status.message == "OK"
    }

    def "invalid date is rejected"() {
        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456C/financialstatus")
            .param("applicationRaisedDate", "2015-03-xx")
            .param("dependants", "1")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: Application raised date is invalid"
    }

    def "future date is rejected"() {
        given:
        String tomorrow = now().plusDays(1).format(ISO_LOCAL_DATE);

        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456C/financialstatus")
            .param("applicationRaisedDate", tomorrow)
            .param("dependants", "1")
        )

        then:
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isBadRequest())
        jsonContent.status.message == "Parameter error: applicationRaisedDate"
    }

    def "monthly payment uses 182 days in start date calculation"() {
        given:
        1 * earningsService.lookup(_, _) >> new TemporaryMigrationFamilyApplication(getIndividual(), LocalDate.now(), "A", true)
        1 * individualService.lookup(_, _, _) >> new IncomeProvingResponse(getIndividual(), getConsecutiveIncomes(), "9600", "M1")

        when:
        def response = mockMvc.perform(get("/incomeproving/v1/individual/AA123456A/financialstatus")
            .param("applicationRaisedDate", "2015-09-23")
            .param("dependants", "1")
        )

        then:

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        response.andExpect(status().isOk())
        jsonContent.categoryCheck.assessmentStartDate == "2015-03-25"

    }

}
