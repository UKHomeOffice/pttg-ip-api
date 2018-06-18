//package uk.gov.digital.ho.proving.income.api.test
//
//import spock.lang.Specification
//import uk.gov.digital.ho.proving.income.api.IncomeValidator
//
//import java.time.LocalDate
//import java.time.Month
//
//import static MockDataUtils.getDate
//
//class DateCalculationSpec extends Specification {
//
//    def "Check we get 1 month difference between 23/01/2015 and 23/02/2015"() {
//
//        given:
//        LocalDate from = getDate(2015, Month.JANUARY,23)
//        LocalDate to = getDate(2015,Month.FEBRUARY,23)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 1
//
//    }
//
//    def "Check we get 1 month difference between 14/07/2015 and 17/06/2015"() {
//
//        given:
//        LocalDate from = getDate(2015,Month.JUNE,17)
//        LocalDate to = getDate(2015,Month.JULY,14)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 1
//
//    }
//
//
//    def "Check we get 1 month difference between 23/01/2015 and 25/02/2015"() {
//
//        given:
//        LocalDate from = getDate(2015,Month.JANUARY,23)
//        LocalDate to = getDate(2015,Month.FEBRUARY,20)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 1
//
//    }
//
//    def "Check we get 1 month difference between 1/01/2015 and 28/02/2015"() {
//
//        given:
//        LocalDate from = getDate(2015,Month.JANUARY,1)
//        LocalDate to = getDate(2015,Month.FEBRUARY,28)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 1
//
//    }
//
//
//    def "Check we get 2 month difference between 1/12/2014 and 28/02/2015"() {
//
//        given:
//        LocalDate from = getDate(2014,Month.DECEMBER,1)
//        LocalDate to = getDate(2015,Month.FEBRUARY,28)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 2
//
//    }
//
//    def "Check we get 14 month difference between 1/12/2014 and 28/02/2016"() {
//
//        given:
//        LocalDate from = getDate(2014,Month.DECEMBER,1)
//        LocalDate to = getDate(2016,Month.FEBRUARY,28)
//
//        when:
//        long months = IncomeValidator.getDifferenceInMonthsBetweenDates(to, from)
//
//        then:
//        months == 14
//
//    }
//
//
//}
