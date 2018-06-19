import ch.qos.logback.classic.filter.ThresholdFilter
import net.logstash.logback.composite.loggingevent.*
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

def appName = "pttg-ip-api"
def version = "0.1.0"

// Add a status listener to record the state of the logback configuration when the logging system is initialised.
statusListener(OnConsoleStatusListener)

appender("STDOUT", ConsoleAppender) {
    encoder(LoggingEventCompositeJsonEncoder) {
        providers(LoggingEventJsonProviders) {
            pattern(LoggingEventPatternJsonProvider) {
                pattern = """{ "appName": "${appName}", "appVersion":"${
                    version
                }", "level": "%-5level", "thread": "%thread", "logger": "%logger{36}" }"""
            }
            message(MessageJsonProvider)
            mdc(MdcJsonProvider)
            arguments(ArgumentsJsonProvider)
            logstashMarkers(LogstashMarkersJsonProvider)
            timestamp(LoggingEventFormattedTimestampJsonProvider)
            stackTrace(StackTraceJsonProvider)
        }
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

appender("FILE", FileAppender) {
    file = "income-record-service-live-proving.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-4relative [%thread] %-5level %logger{35} - %msg%n"
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

logger("uk.gov.digital.ho.proving.income.hmrc.IncomeRecordServiceNotProductionResponseLogger", DEBUG, ["FILE"], additivity = false)

// Define logging levels for specific packages
logger("org.eclipse.jetty", WARN)
logger("org.springframework", WARN)
logger("org.hibernate", WARN)

root(DEBUG, ["STDOUT"])

// Check config file every 30 seconds and reload if changed
scan("30 seconds")
