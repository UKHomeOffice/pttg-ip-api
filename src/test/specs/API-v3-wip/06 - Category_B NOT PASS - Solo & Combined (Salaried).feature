Feature: Category B Financial Requirement - Solo & Combined Applications for Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3840 (Category B - Salaried)
    # Scenarios where Category B Salaried Assessments do not pass
    # Employment check threshold is required to be met within the immediate 32 days from and inclusive of application raised date (£1550 no dependents, £1866.70 one dependent, £2066.70 two dependents)
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total of all payments within each consecutive month within the 12-month range

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600. Monthly threshold is set at £1550
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400. Monthly threshold is set at £1866.70
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800. Monthly threshold is set at £2066.70

    ##############

    Scenario: 01 Laura has no dependents. Her income history shows a payment within the employment check in the immediate 32 days but does not meet the threshold (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1549.99 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.01 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL823678B  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response    | HTTP Status               | 200                     |
            | Applicant        | National Insurance Number | PL823678B               |
            | Employment Check | Financial requirement met | false                   |
            | Employment Check | Failure Reason            | EMPLOYMENT_CHECK_FAILED |
            | Employment Check | Application Raised date   | 2018-04-30              |
            | Employment Check | Assessment Start Date     | 2018-03-30              |
            | Employment Check | Threshold                 | 1550.00                 |
            | Employment Check | Employer Name - PL823678B | Flying Pizza Ltd        |

     ##############

    Scenario: 02 Derek has no dependents. His income history shows no payment within the employment check in the immediate 32 days (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-29 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | KK776546A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response    | HTTP Status               | 200                     |
            | Applicant        | National Insurance Number | KK776546A               |
            | Employment Check | Financial requirement met | false                   |
            | Employment Check | Failure Reason            | EMPLOYMENT_CHECK_FAILED |
            | Employment Check | Application Raised date   | 2018-04-30              |
            | Employment Check | Assessment Start Date     | 2018-03-30              |
            | Employment Check | Threshold                 | 1550.00                 |
            | Employment Check | Employer Name - KK776546A | Flying Pizza Ltd        |
        
    ##############

    Scenario: 03 Kayleigh has no dependents. Her income history shows a payment within the employment check in the immediate 32 days that meets the threshold (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.01 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1549.99 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL823678B  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                           |
            | Applicant                 | National Insurance Number | PL823678B                     |
            | Category B salaried       | Financial requirement met | false                         |
            | Category B salaried       | Failure Reason            | CATB_SALARIED_BELOW_THRESHOLD |
            | Category B salaried       | Application Raised date   | 2018-04-30                    |
            | Category B salaried       | Assessment Start Date     | 2017-04-30                    |
            | Category B salaried       | Threshold                 | 18600                         |
            | Category B salaried       | Employer Name - PL823678B | Flying Pizza Ltd              |

    ##############

    Scenario: 04 Harry has no dependents. His income history shows a payment within the employment check in the immediate 32 days but does not meet the threshold (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts but the overall annual threshold is not met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1549.99 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JJ823678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                           |
            | Applicant           | National Insurance Number | JJ823678A                     |
            | Category B salaried | Financial requirement met | false                         |
            | Category B salaried | Failure Reason            | CATB_SALARIED_BELOW_THRESHOLD |
            | Category B salaried | Application Raised date   | 2018-04-30                    |
            | Category B salaried | Assessment Start Date     | 2017-04-30                    |
            | Category B salaried | Threshold                 | 18600                         |
            | Category B salaried | Employer Name - JJ823678A | Flying Pizza Ltd              |

    ##############

    Scenario: 05 Chrissy has no dependents. Her income history shows a payment within the employment check in the immediate 32 days that meets the threshold (2018-03-30). The total monthly payments in all the other months meet the annual threshold but one month is missing

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
#           |            |         |             |              |                |                  |
            | 2017-08-25 | 3100.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | BB827766D  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                    |
            | Applicant           | National Insurance Number | BB827766D              |
            | Category B salaried | Financial requirement met | false                  |
            | Category B salaried | Failure Reason            | NON_CONSECUTIVE_MONTHS |
            | Category B salaried | Application Raised date   | 2018-04-30             |
            | Category B salaried | Assessment Start Date     | 2017-04-30             |
            | Category B salaried | Threshold                 | 18600                  |
            | Category B salaried | Employer Name - BB827766D | Flying Pizza Ltd       |

    ##############

    Scenario: 06 Donald has no dependents. His income history shows a payment within the employment check in the immediate 32 days but only passes when supplemented by their partner (2018-03-30). The total monthly payments in all the other months meet the annual threshold but the last two months do not meet the monthly threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 |  775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 |  775.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 |  775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 |  775.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 |  775.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 |  775.00 |             | 12           | RM/Ref3        | Reliable Motors  |
            | 2018-03-27 |  775.00 |             | 11           | RM/Ref3        | Reliable Motors  |
            | 2018-02-25 |  775.00 |             | 10           | RM/Ref3        | Reliable Motors  |
            | 2018-01-30 |  775.00 |             | 09           | RM/Ref3        | Reliable Motors  |
            | 2017-12-27 |  775.00 |             | 08           | RM/Ref3        | Reliable Motors  |
            | 2017-11-29 |  775.00 |             | 07           | RM/Ref3        | Reliable Motors  |
            | 2017-10-27 |  775.00 |             | 06           | RM/Ref3        | Reliable Motors  |
            | 2017-09-25 |  775.00 |             | 05           | QE/Ref4        | Quality Estates  |
            | 2017-08-28 |  775.00 |             | 04           | QE/Ref4        | Quality Estates  |
            | 2017-07-27 |  775.00 |             | 03           | RM/Ref3        | Reliable Motors  |
            | 2017-06-30 |  775.00 |             | 02           | RM/Ref3        | Reliable Motors  |
            | 2017-05-29 |  775.00 |             | 01           | RM/Ref3        | Reliable Motors  |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant                   | RR223611A  |
            | NINO - Partner                     | GG199882B  |
            | Application Raised Date            | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                              |
            | Applicant           | National Insurance Number | RR223611A                        |
            | Partner             | National Insurance Number | GG199882B                        |
            | Category B salaried | Financial requirement met | false                            |
            | Category B salaried | Failure Reason            | CATB_SALARIED_BELOW_THRESHOLD    |
            | Category B salaried | Application Raised date   | 2018-04-30                       |
            | Category B salaried | Assessment Start Date     | 2017-04-30                       |
            | Category B salaried | Threshold                 | 18600                            |
            | Category B salaried | Employer Name - RR223611A | Flying Pizza Ltd                 |
            | Category B salaried | Employer Name - GG199882B | Reliable Motors, Quality Estates |
  
   ##############

    Scenario: 07 Sarah has no dependents. Her income history shows a payment within the employment check in the immediate 32 days but does not meet the threshold even when supplemented with her partner (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-01 |  775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 |  775.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 |  775.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 |  775.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 |  775.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 |  774.99 |             | 12           | RM/Ref3        | Reliable Motors  |
            | 2018-03-27 |  775.01 |             | 11           | RM/Ref3        | Reliable Motors  |
            | 2018-02-25 |  775.00 |             | 10           | RM/Ref3        | Reliable Motors  |
            | 2018-01-30 |  775.00 |             | 09           | RM/Ref3        | Reliable Motors  |
            | 2017-12-27 |  775.00 |             | 08           | RM/Ref3        | Reliable Motors  |
            | 2017-11-29 |  775.00 |             | 07           | RM/Ref3        | Reliable Motors  |
            | 2017-10-27 |  775.00 |             | 06           | RM/Ref3        | Reliable Motors  |
            | 2017-09-25 |  775.00 |             | 05           | QE/Ref4        | Quality Estates  |
            | 2017-08-28 |  775.00 |             | 04           | QE/Ref4        | Quality Estates  |
            | 2017-07-27 |  775.00 |             | 03           | RM/Ref3        | Reliable Motors  |
            | 2017-06-30 |  775.00 |             | 02           | RM/Ref3        | Reliable Motors  |
            | 2017-05-29 |  775.00 |             | 01           | RM/Ref3        | Reliable Motors  |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant                   | PK676311C  |
            | NINO - Partner                     | SZ111882A  |
            | Application Raised Date            | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                              |
            | Applicant           | National Insurance Number | PK676311C                        |
            | Partner             | National Insurance Number | SZ111882A                        |
            | Category B salaried | Financial requirement met | false                            |
            | Category B salaried | Failure Reason            | EMPLOYMENT_CHECK_FAILED          |
            | Category B salaried | Application Raised date   | 2018-04-30                       |
            | Category B salaried | Assessment Start Date     | 2017-04-30                       |
            | Category B salaried | Threshold                 | 18600                            |
            | Category B salaried | Employer Name - PK676311C | Flying Pizza Ltd                 |
            | Category B salaried | Employer Name - SZ111882A | Reliable Motors, Quality Estates |

    ##############

    Scenario: 08 Jake has one dependent. His income history shows a payment within the employment check in the immediate 32 that meets the threshold (2018-03-30). The total monthly payments in all the other months do not meet the annual threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1866.70 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1866.70 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1866.70 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1866.70 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1866.70 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1866.70 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1866.70 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1866.70 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1866.70 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1866.70 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1866.70 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1866.69 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL823678B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                           |
            | Applicant                 | National Insurance Number | PL823678B                     |
            | Category B salaried       | Financial requirement met | false                         |
            | Category B salaried       | Failure Reason            | CATB_SALARIED_BELOW_THRESHOLD |
            | Category B salaried       | Application Raised date   | 2018-04-30                    |
            | Category B salaried       | Assessment Start Date     | 2017-04-30                    |
            | Category B salaried       | Threshold                 | 22400                         |
            | Category B salaried       | Employer Name - PL823678B | Flying Pizza Ltd              |

    ##############

    Scenario: 09 Joanne has two dependents. Her income history shows a payment within the employment check in the immediate 32 that meets the threshold (2018-03-30). The total monthly payments in all the other months do not meet the annual threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 2066.70 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2066.70 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2066.70 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2066.70 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2066.70 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2066.70 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2066.70 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2066.70 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2066.70 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 2066.70 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2066.70 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 2066.69 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL823678B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                           |
            | Applicant                 | National Insurance Number | PL823678B                     |
            | Category B salaried       | Financial requirement met | false                         |
            | Category B salaried       | Failure Reason            | CATB_SALARIED_BELOW_THRESHOLD |
            | Category B salaried       | Application Raised date   | 2018-04-30                    |
            | Category B salaried       | Assessment Start Date     | 2017-04-30                    |
            | Category B salaried       | Threshold                 | 24800                         |
            | Category B salaried       | Employer Name - PL823678B | Flying Pizza Ltd              |
