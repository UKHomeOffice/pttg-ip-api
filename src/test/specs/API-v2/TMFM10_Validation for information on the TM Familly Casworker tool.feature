Feature: Validation of the API fields and data

    National Insurance Numbers (NINO) - Format and Security: A NINO is made up of two letters, six numbers and a final letter (which is always A, B, C, or D)
    Date formats: Format should be yyyy-MM-dd

    Background: Thresholds are configured to default values
        Given The yearly threshold is configured to 18600:
        And The single dependant yearly threshold is configured to 22400:
        And The remaining dependant increment is configured to 2400:

###################################### Section - Validation on the NINO ######################################

    Scenario: The API is not provided with an NINO (National Insurance Number)
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        |            |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

    Scenario: The API provides incorrect National Insurance Number prefixed with two digits
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | 11123456A  |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

    Scenario: The API provides incorrect National Insurance Number with two characters in the middle
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | QQ12HR56A  |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

    Scenario: The API provides incorrect National Insurance Number with the last digit being a number
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP1235560  |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

    Scenario: The API provides incorrect National Insurance Number with the last digit not being a valid character
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP123556E  |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

    Scenario: The API provides incorrect National Insurance Number as not 9 characters
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | QQ1235A    |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                 |
            | Status        | code        | 0004                |
            | Status        | message     | Error: Invalid NINO |

###################################### Section - Validation on the Application Raised Date ######################################

    Scenario: The API provides an incorrect Application Raised Date (Day format)
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP129956A  |
            | Application Raised Date | 2015-01-85 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                    |
            | Status        | code        | 0004                   |
            | Status        | message     | Error: Invalid request |

    Scenario: The API provides an incorrect Application Raised Date (Month format)
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP129956A  |
            | Application Raised Date | 2015-13-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                    |
            | Status        | code        | 0004                   |
            | Status        | message     | Error: Invalid request |

    Scenario: The API provides an incorrect Application Raised Date (Year format)
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP129956A  |
            | Application Raised Date | 201H-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                    |
            | Status        | code        | 0004                   |
            | Status        | message     | Error: Invalid request |

    Scenario: The API provides a blank Application Raised date
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP128856A |
            | Application Raised Date |           |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                          |
            | Status        | code        | 0004                         |
            | Status        | message     | Error: applicationRaisedDate |

 #New scenario - Added 24.05.16
    Scenario: The API prevents a future date as the Application Raised Date
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP125556A  |
            | Application Raised Date | 2027-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                          |
            | Status        | code        | 0004                         |
            | Status        | message     | Error: applicationRaisedDate |


###################################### Section - Validation on the Dependants field ######################################

    Scenario: The API provides Dependants with a character
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP128856A  |
            | Application Raised Date | 2015-01-01 |
            | Dependants              | H          |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                    |
            | Status        | code        | 0004                   |
            | Status        | message     | Error: Invalid request |

    Scenario: The API provides Dependants with a negative number
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP128856A  |
            | Application Raised Date | 2015-01-01 |
            | Dependants              | -3         |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                                     |
            | Status        | code        | 0004                                    |
            | Status        | message     | Error: Dependants cannot be less than 0 |

    Scenario: The API provides Dependants with 3 digits
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP128856A  |
            | Application Raised Date | 2015-01-01 |
            | Dependants              | 100        |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                                      |
            | Status        | code        | 0004                                     |
            | Status        | message     | Error: Dependants cannot be more than 99 |

###################################### Section - NINO does not exist ######################################

    Scenario: The API provides a NINO that does not exist in the system
        Given A service is consuming the Income Proving TM Family API
        And HMRC has no matching record
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP128856A  |
            | Application Raised Date | 2015-01-01 |
            | Dependants              | 3          |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 404                           |
            | Status        | code        | 0009                          |
            | Status        | message     | Resource not found: SP128**** |

    Scenario: The partner's NINO is not found by HMRC, correct NINO is reported in error
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 3066.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
        And the applicants partner has no matching record
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | NINO - Partner          | BB345678A  |
            | Application Raised Date | 2015-01-01 |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 404                           |
            | Status        | code        | 0009                          |
            | Status        | message     | Resource not found: BB345**** |

    Scenario: The API is provided with a valid NINO and a future application raised date
        Given A service is consuming the Income Proving TM Family API
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP987654A  |
            | Application Raised Date | 2026-12-21 |
            | Dependants              | 3          |
        Then The Income Proving TM Family API provides the following result:
            | Http Response | HTTP Status | 400                          |
            | Status        | code        | 0004                         |
            | Status        | message     | Error: applicationRaisedDate |
