Feature: Category A & B Financial Requirement - Solo & Combined Applications for Monthly Salary Assessments

    # JIRA STORIES: EE-6347 (Total Monthly Payments for Monthly Salary Assessments)
    # Scenarios where Categories A & B Monthly Salary Assessments pass
    # Multiple payments within the same month can be added together and used before the comparison against the annual check takes place
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date (Cat B only)
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating Category A & B Monthly Salary income for comparison against the threshold is:
    #   - Total of all payments within each consecutive month within the 6-month (CAT A) or 12-month (CAT B) range
    # If the assessment is being made against Monthly Salary employment then the following applies:
    #   - Monthly payments alone – Achieved by meeting a minimum consistent value of £1,550 per month
    #   - Weekly payments alone – Achieved by meeting a minimum consistent value of £358.00 per week
    #   - Fortnightly payments alone – Achieved by meeting a minimum consistent value of £715.00 per fortnight
    #   - 4-Weekly payments alone– Achieved by meeting a minimum consistent value of £1430 per 4-weekly
    #   - Mixture of any of the above

    Scenario: Category A. No dependents. Annual Check Met. Multiple payments in the month at the start of the assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-27 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-30 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-28 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-31 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-31 | 550.00  |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200              |
            | Applicant                 | National Insurance Number | AA345678A        |
            | Category A Monthly Salary | Financial requirement met | true             |
            | Category A Monthly Salary | Application Raised date   | 2018-04-30       |
            | Category A Monthly Salary | Assessment Start Date     | 2017-10-30       |
            | Category A Monthly Salary | Threshold                 | 1550.00          |
            | Category A Monthly Salary | Employer Name - AA345678A | Flying Pizza Ltd |

##############

    Scenario: Category A. No dependents. Annual Check Met. Multiple payments of weekly frequency in the month at the end of the assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 550.00  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 1000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200              |
            | Applicant                 | National Insurance Number | AA345678A        |
            | Category A Monthly Salary | Financial requirement met | true             |
            | Category A Monthly Salary | Application Raised date   | 2018-04-30       |
            | Category A Monthly Salary | Assessment Start Date     | 2017-10-30       |
            | Category A Monthly Salary | Threshold                 | 1550.00          |
            | Category A Monthly Salary | Employer Name - AA345678A | Flying Pizza Ltd |

##############

    Scenario: Category A. One dependent. Annual Check Met. Multiple monthly payments in the month at the end of the assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1866.70 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1866.70 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1866.70 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1866.70 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1866.70 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 66.70   |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 1800.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200              |
            | Applicant                 | National Insurance Number | AA345678A        |
            | Category A Monthly Salary | Financial requirement met | true             |
            | Category A Monthly Salary | Application Raised date   | 2018-04-30       |
            | Category A Monthly Salary | Assessment Start Date     | 2017-10-30       |
            | Category A Monthly Salary | Threshold                 | 1866.67          |
            | Category A Monthly Salary | Employer Name - AA345678A | Flying Pizza Ltd |

##############

    Scenario: Category A. No dependents. Annual Check Met. Multiple payments of weekly frequency in the month at the start of the assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 387.49  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.51  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.52  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.48  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200              |
            | Applicant                 | National Insurance Number | AA345678A        |
            | Category A Monthly Salary | Financial requirement met | true             |
            | Category A Monthly Salary | Application Raised date   | 2018-04-30       |
            | Category A Monthly Salary | Assessment Start Date     | 2017-10-30       |
            | Category A Monthly Salary | Threshold                 | 1550.00          |
            | Category A Monthly Salary | Employer Name - AA345678A | Flying Pizza Ltd |


    # Defect EE-9307
    @WIP
    Scenario: Cat B - No dependents - Employment check passes with partner - Multiple employments combined to pass
        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 475.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 300.00 |             | 11           | FP/Ref2        | Crazy Pizza Ltd  |
            | 2018-02-28 | 775.00 |             | 10           | FP/Ref2        | Crazy Pizza Ltd  |
            | 2018-01-31 | 775.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 775.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 775.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 775.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 775.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 775.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 775.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 775.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-30 | 775.00 |             | 12           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 775.00 |             | 11           | RM/Ref3        | Reliable Motors |
            | 2018-02-25 | 775.00 |             | 10           | RM/Ref3        | Reliable Motors |
            | 2018-01-30 | 775.00 |             | 09           | RM/Ref3        | Reliable Motors |
            | 2017-12-27 | 775.00 |             | 08           | RM/Ref3        | Reliable Motors |
            | 2017-11-29 | 775.00 |             | 07           | RM/Ref3        | Reliable Motors |
            | 2017-10-27 | 775.00 |             | 06           | RM/Ref3        | Reliable Motors |
            | 2017-09-25 | 775.00 |             | 05           | QE/Ref4        | Quality Estates |
            | 2017-08-28 | 775.00 |             | 04           | QE/Ref4        | Quality Estates |
            | 2017-07-27 | 775.00 |             | 03           | RM/Ref3        | Reliable Motors |
            | 2017-06-30 | 775.00 |             | 02           | RM/Ref3        | Reliable Motors |
            | 2017-05-29 | 775.00 |             | 01           | RM/Ref3        | Reliable Motors |
        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | RR223611A  |
            | NINO - Partner          | GG199882B  |
            | Application Raised Date | 2018-04-30 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                               |
            | Applicant                 | National Insurance Number | RR223611A                         |
            | Partner                   | National Insurance Number | GG199882B                         |
            | Category A Monthly Salary | Financial requirement met | false                             |
            | Category A Monthly Salary | Failure Reason            | MULTIPLE_EMPLOYERS                |
            | Category A Monthly Salary | Application Raised date   | 2018-04-30                        |
            | Category A Monthly Salary | Assessment Start Date     | 2017-10-30                        |
            | Category A Monthly Salary | Threshold                 | 1550.00                           |
            | Category A Monthly Salary | Employer Name - RR223611A | Flying Pizza Ltd, Crazy Pizza Ltd |
            | Category B non salaried   | Financial requirement met | true                              |
            | Category B non salaried   | Application Raised date   | 2018-04-30                        |
            | Category B non salaried   | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried   | Threshold                 | 18600                             |
            | Category B non salaried   | Employer Name - RR223611A | Flying Pizza Ltd, Crazy Pizza Ltd |
            | Category B non salaried   | Employer Name - GG199882B | Reliable Motors, Quality Estates  |
            | Category B salaried       | Financial requirement met | true                              |
            | Category B salaried       | Failure Reason            | CATB_SALARIED_PASSED              |
            | Category B salaried       | Application Raised date   | 2018-04-30                        |
            | Category B salaried       | Assessment Start Date     | 2017-04-30                        |
            | Category B salaried       | Threshold                 | 18600                             |
            | Category B salaried       | Employer Name - RR223611A | Flying Pizza Ltd,Crazy Pizza Ltd  |
            | Category B salaried       | Employer Name - GG199882B | Reliable Motors, Quality Estates  |
