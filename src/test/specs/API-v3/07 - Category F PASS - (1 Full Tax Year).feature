Feature: Category F Financial Requirement - Self-Assessment - Solo & Combined Applications

    # JIRA STORIES: EE-3842 (Self-Assessment - 1 Full Tax Year)
    # Scenarios where Self-Assessment income passes against a single full tax year
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Tax years are defined as running from 6th April to April 5th

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600 within the last full tax year
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400 within the last full tax year
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800 within the last full tax year

    Scenario: 01 Charlotte has no dependents. Her income history shows a self assessment payment in the last full tax year that meets the threshold
        Given HMRC has the following Self Assessment Returns for nino TK047457B:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 18600.00               |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TK047457B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200        |
            | Category F Self-Assessment Income | Financial requirement met | true       |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30 |
            | Category F Self-Assessment Income | Threshold                 | 18600      |
            | Applicant                         | National Insurance Number | TK047457B  |

    Scenario: Teresa has no dependents and her self assessment returns is below the threshold but passes when combined with her partners
        Given HMRC has the following Self Assessment Returns for nino OB666650A:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 9300.00                |
        And HMRC has the following Self Assessment Returns for nino BJ892995A:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 9300.00                |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | OB666650A  |
            | NINO - Partner          | BJ892995A  |
            | Application Raised Date | 2017-04-06 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200        |
            | Category F Self-Assessment Income | Financial requirement met | true       |
            | Category F Self-Assessment Income | Application Raised date   | 2017-04-06 |
            | Category F Self-Assessment Income | Threshold                 | 18600      |
            | Applicant                         | National Insurance Number | OB666650A  |
            | Partner                           | National Insurance Number | BJ892995A  |

    Scenario: 03 Jermaine has one dependent. His income history shows a self assessment payment in the last full tax year that meets the threshold
        Given HMRC has the following Self Assessment Returns for nino ZR507150B:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 22400.00               |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | ZR507150B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200        |
            | Category F Self-Assessment Income | Financial requirement met | true       |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30 |
            | Category F Self-Assessment Income | Threshold                 | 22400      |
            | Applicant                         | National Insurance Number | ZR507150B  |

    Scenario: 04 Belinda has two dependents. Her income history shows a self assessment payment in the last full tax year that meets the threshold
        Given HMRC has the following Self Assessment Returns for nino CY893804C:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 24800.00               |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | CY893804C  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                     | HTTP Status               | 200        |
            | Category F Self-Assessment Income | Financial requirement met | true       |
            | Category F Self-Assessment Income | Application Raised date   | 2018-04-30 |
            | Category F Self-Assessment Income | Threshold                 | 24800      |
            | Applicant                         | National Insurance Number | CY893804C  |
