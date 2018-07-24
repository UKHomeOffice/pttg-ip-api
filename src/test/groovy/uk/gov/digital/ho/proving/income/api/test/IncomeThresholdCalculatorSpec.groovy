package uk.gov.digital.ho.proving.income.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.income.api.IncomeThresholdCalculator

class IncomeThresholdCalculatorSpec extends Specification {

    def "throw error for negative number of dependants"() {
        given:
        IncomeThresholdCalculator calculator
        when:
        calculator = new IncomeThresholdCalculator(-1);
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        thrown IllegalArgumentException
    }

    def "calculate thresholds for zero dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(0);
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(1550.00)) == 0
    }

    def "calculate thresholds for one dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(1);
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(1866.67)) == 0
    }

    def "calculate thresholds for two dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(2);
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(2066.67)) == 0
    }

    def "calculate thresholds for three dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(3)
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(2266.67)) == 0
    }

    def "calculate thresholds for four dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(4)
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(2466.67)) == 0
    }

    def "calculate thresholds for five dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(5)
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(2666.67)) == 0
    }

    def "calculate thresholds for six dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(6)
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(2866.67)) == 0
    }

    def "calculate thresholds for seven dependant"() {
        given:
        IncomeThresholdCalculator calculator = new IncomeThresholdCalculator(7)
        when:
        BigDecimal threshold = calculator.getMonthlyThreshold()
        then:
        threshold.compareTo(BigDecimal.valueOf(3066.67)) == 0
    }

}
