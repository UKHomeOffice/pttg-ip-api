Feature: Category B Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3841 (Category B - Non-Salaried)
    # Scenarios where Category B Non-Salaried Assessments pass
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by number of months paid, multiply by 12

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800

    Scenario: Category A. No dependents. Annual Check Met. Multiple payments of different frequency in the month at the start of the assessment range. Test to Add together monthly amounts.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-17 |  275.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-17 |  275.00 | 02          | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-17 | 1000.00 | 01          | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-09-29 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-31 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-31 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-04-30       |
            | Category B non salaried | Assessment Start Date     | 2017-04-30       |
            | Category B non salaried | Threshold                 | 18600            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |
