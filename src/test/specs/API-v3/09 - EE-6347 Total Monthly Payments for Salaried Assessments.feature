Feature: Category A & B Financial Requirement - Solo & Combined Applications for Monthly Salary Assessments

    # JIRA STORIES: EE-6347 (Total Monthly Payments for Monthly Salary Assessments)
    # Scenarios where Categories A & B Monthly Salary Assessments pass
    # Multiple payments within the same month can be added together and used before the comparison against the annual check takes place
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating Category A & B Monthly Salary income for comparison against the threshold is:
    #       - Total of all payments within each consecutive month within the 6-month (CAT A) or 12-month (CAT B) range
    # If the assessment is being made against Monthly Salary employment then the following applies:
    #       - Monthly payments alone – Achieved by meeting a minimum consistent value of £1,550 per month
    #       - Weekly payments alone – Achieved by meeting a minimum consistent value of £358.00 per week
    #       - Fortnightly payments alone – Achieved by meeting a minimum consistent value of £715.00 per fortnight
    #       - 4-Weekly payments alone– Achieved by meeting a minimum consistent value of £1430 per 4-weekly
    #       - Mixture of any of the above

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
            | 2018-04-27 | 387.50  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.50  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.50  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-27 | 387.50  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
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
