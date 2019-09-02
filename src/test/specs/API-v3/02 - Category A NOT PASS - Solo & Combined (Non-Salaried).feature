Feature: Category A Financial Requirement - Solo & Combined Applications

    # JIRA STORY: EE-3838
    # Scenarios where Category A Non-Salaried Assessments do not pass
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by 6, multiply by 12

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800
    Background: Thresholds are configured to default values
        Given The yearly threshold is configured to 18600:
        And The single dependant yearly threshold is configured to 22400:
        And The remaining dependants increment is configured to 2400:

    # 01 Cheryl has no dependents. Her income history shows payments in 6 months that do not meet the threshold. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has no dependants and the income is below threshold
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1299.50 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category A Non Salaried | Financial requirement met | false                             |
            | Category A Non Salaried | Failure Reason            | CATA_NON_SALARIED_BELOW_THRESHOLD |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                        |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                        |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category A Non Salaried | Threshold                 | 18600                             |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |


    # 02 Ashley has one dependent. His income history shows payments in 7 months that do not meet the threshold. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has one dependant and the income is below threshold
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 500.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 4000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1199.99 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 500.00  |             | 10           | FP/Ref1        | Flying Pizza Ltd |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category A Non Salaried | Financial requirement met | false                             |
            | Category A Non Salaried | Failure Reason            | CATA_NON_SALARIED_BELOW_THRESHOLD |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                        |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                        |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category A Non Salaried | Threshold                 | 22400                             |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |


    # 03 Carla has no dependents. Her income history shows payments in 7 months that meet the threshold however one payment is just outside the assessment range. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has no dependants and one payment is out of the assessment range
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 2000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-01-31 | 1300.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 999.99  |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-29 | 000.01  |             | 10           | FP/Ref1        | Flying Pizza Ltd |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category A Non Salaried | Financial requirement met | false                             |
            | Category A Non Salaried | Failure Reason            | CATA_NON_SALARIED_BELOW_THRESHOLD |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                        |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                        |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category A Non Salaried | Threshold                 | 18600                             |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |


    # 04 Kayleigh has no dependants. Her income history shows payments that only meet the threshold with combined income from multiple employers. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has no dependants and the income is only over threshold with multiple employers
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer          |
            | 2018-03-29 | 2000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd  |
            | 2018-02-28 | 2000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd  |
            | 2018-01-31 | 2000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd  |
            | 2017-12-29 | 2000.00 |             | 12           | FP/Ref2        | Derek's Autos Ltd |
            | 2017-11-30 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd  |
            | 2017-10-30 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd  |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                                 |
            | Category A Non Salaried | Financial requirement met | false                               |
            | Category A Non Salaried | Failure Reason            | MULTIPLE_EMPLOYERS                  |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                          |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                          |
            | Applicant               | National Insurance Number | AA345678A                           |
            | Category A Non Salaried | Threshold                 | 18600                               |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd, Derek's Autos Ltd |


    # 05 Barry has no dependents. His income history shows a payment in some months with gaps but do not meet the threshold even when it is supplemented by a partners income also having gaps. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has no dependants and payment with gaps but below threshold, even with partner income
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 2450.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 550.00  |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-27 | 1000.00 |             | 04           | HO/Ref9        | The Home Office |
            | 2018-02-28 | 1000.00 |             | 02           | HO/Ref9        | The Home Office |
            | 2018-01-31 | 2000.00 |             | 01           | HO/Ref9        | The Home Office |
            | 2017-12-23 | 299.99  |             | 12           | HO/Ref9        | The Home Office |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | NINO - Partner          | GE345678A  |
            | Application Raised Date | 2018-04-30 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category A Non Salaried | Financial requirement met | false                             |
            | Category A Non Salaried | Failure Reason            | CATA_NON_SALARIED_BELOW_THRESHOLD |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                        |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                        |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Partner                 | National Insurance Number | GE345678A                         |
            | Category A Non Salaried | Threshold                 | 18600                             |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |
            | Category A Non Salaried | Employer Name - GE345678A | The Home Office                   |


    #  06 Sherilyn has two dependents. Her income history shows a full contingent of payments over a 12 month period. All 12 months average as a pass against the threshold but the payments within the 6 month period do not. Assessment range 2018-04-30 to 2017-10-30
    Scenario: Applicant has two dependants with income above threshold over 12 month period but below threshold in the last 6 months
        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 400.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 300.00  |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-23 | 300.00  |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-26 | 300.00  |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-27 | 50.00   |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-28 | 49.99   |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-31 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-30 | 1000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-27 | 2500.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1000.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-31 | 1000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-30 | 1000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | GE345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category A Non Salaried | Financial requirement met | false                             |
            | Category A Non Salaried | Failure Reason            | CATA_NON_SALARIED_BELOW_THRESHOLD |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                        |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30                        |
            | Applicant               | National Insurance Number | GE345678A                         |
            | Category A Non Salaried | Threshold                 | 24800                             |
            | Category A Non Salaried | Employer Name - GE345678A | Flying Pizza Ltd                  |


    Scenario: Applicant has no dependents with a self-assessment payment in the last full tax year that meets the threshold
        Given HMRC has the following Self Assessment Returns for nino TK047457B:
            | TaxYear | Self Employment Profit |
            | 2017-18 | 18600.00       |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TK047457B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 0          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                |
            | Category A Non Salaried | Financial requirement met | false              |
            | Category A Non Salaried | Failure Reason            | NOT_ENOUGH_RECORDS |
            | Category A Non Salaried | Application Raised date   | 2018-04-30         |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30         |
            | Applicant               | National Insurance Number | TK047457B          |
            | Category A Non Salaried | Threshold                 | 18600              |

