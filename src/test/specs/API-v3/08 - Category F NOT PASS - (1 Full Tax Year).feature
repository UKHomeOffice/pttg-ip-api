Feature: Category F Financial Requirement - Self-Assessment - Solo & Combined Applications

    # JIRA STORIES: EE-3842 (Self-Assessment - 1 Full Tax Year)
    # Scenarios where Self-Assessment income does not pass against a single full tax year
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Tax years are defined as running from 6th April to April 5th

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600 within the last full tax year
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400 within the last full tax year
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800 within the last full tax year

    Scenario: No dependents. Self-Assessment payment in the last full tax year that does not meet the threshold
        Given HMRC has the following Self Assessment Returns for nino SP123456A:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 18599.99       |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP123456A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200                             |
            | Category F Self-Assessment Income | Financial requirement met | false                           |
            | Category F Self-Assessment Income | Failure Reason            | SELF_ASSESSMENT_ONE_YEAR_FAILED |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30                      |
            | Category F Self-Assessment Income | Threshold                 | 18600                           |
            | Applicant                         | National Insurance Number | SP123456A                       |

    Scenario: No dependents. Self-Assessment payment in a tax year after the last full tax year that meets the threshold
        Given HMRC has the following Self Assessment Returns for nino NM616732D:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 00.00          |
            | 2016-17 | 18600.00       |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | NM616732D  |
            | Application Raised Date | 2018-04-06 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200                             |
            | Category F Self-Assessment Income | Financial requirement met | false                           |
            | Category F Self-Assessment Income | Failure Reason            | SELF_ASSESSMENT_ONE_YEAR_FAILED |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-06                      |
            | Category F Self-Assessment Income | Threshold                 | 18600                           |
            | Applicant                         | National Insurance Number | NM616732D                       |

    Scenario: No dependents. Self-Assessment payment in the last full tax year that does not meet the threshold and still does not even when supplemented with a partner
        Given HMRC has the following Self Assessment Returns for nino ZW723343A:
            | TaxYear | Self Employment Profit |
            | 2016-17 | 9299.99        |
        And HMRC has the following Self Assessment Returns for nino BZ483260B:
            | TaxYear | Self Employment Profit |
            | 2016-17 | 9300.00        |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | ZW723343A  |
            | NINO - Partner          | BZ483260B  |
            | Application Raised Date | 2017-04-06 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200                             |
            | Category F Self-Assessment Income | Financial requirement met | false                           |
            | Category F Self-Assessment Income | Failure Reason            | SELF_ASSESSMENT_ONE_YEAR_FAILED |
            | Category F Self-Assessment Income | Application Raised date   | 2017-04-06                      |
            | Category F Self-Assessment Income | Threshold                 | 18600                           |
            | Applicant                         | National Insurance Number | ZW723343A                       |
            | Partner                           | National Insurance Number | BZ483260B                       |

    Scenario: One dependent. Self-Assessment payment in the last full tax year that does not meet the threshold
        Given HMRC has the following Self Assessment Returns for nino KH802177D:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 22399.99       |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | KH802177D  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200                             |
            | Category F Self-Assessment Income | Financial requirement met | false                           |
            | Category F Self-Assessment Income | Failure Reason            | SELF_ASSESSMENT_ONE_YEAR_FAILED |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30                      |
            | Category F Self-Assessment Income | Threshold                 | 22400                           |
            | Applicant                         | National Insurance Number | KH802177D                       |

    Scenario: Two dependents. Self-Assessment payment in the last full tax year that does not meet the threshold
        Given HMRC has the following Self Assessment Returns for nino SN332780B:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 24799.99       |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SN332780B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200                             |
            | Category F Self-Assessment Income | Financial requirement met | false                           |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30                      |
            | Category F Self-Assessment Income | Failure Reason            | SELF_ASSESSMENT_ONE_YEAR_FAILED |
            | Category F Self-Assessment Income | Threshold                 | 24800                           |
            | Applicant                         | National Insurance Number | SN332780B                       |
