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
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1549.99 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.01 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PL823678H  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                            |
            | Category                  | B                              |
            | Financial requirement met | false                          |
            | Failure Reason            | Does not meet employment check |
            | Application Raised date   | 2018-04-30                     |
            | Assessment Start Date     | 2017-04-30                     |
            | National Insurance Number | BPL823678H                     |
            | Threshold                 | 18600                          |
            | Employer Name             | Flying Pizza Ltd               |

     ##############

    Scenario: 02 Derek has no dependents. His income history shows no payment within the employment check in the immediate 32 days (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | KK776546F  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                            |
            | Category                  | B                              |
            | Financial requirement met | false                          |
            | Failure Reason            | Does not meet employment check |
            | Application Raised date   | 2018-04-30                     |
            | Assessment Start Date     | 2017-04-30                     |
            | National Insurance Number | BPL823678H                     |
            | Threshold                 | 18600                          |
            | Employer Name             | Flying Pizza Ltd               |

    ##############

    Scenario: 03 Kayleigh has no dependents. Her income history shows a payment within the employment check in the immediate 32 days that meets the threshold (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.01 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1549.99 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PL823678H  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | B                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2018-04-30              |
            | Assessment Start Date     | 2017-04-30              |
            | National Insurance Number | PL823678H               |
            | Threshold                 | 18600                   |
            | Employer Name             | Flying Pizza Ltd        |

    ##############

    Scenario: 04 Harry has no dependents. His income history shows a payment within the employment check in the immediate 32 days but does not meet the threshold (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts but the overall annual threshold is not met.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1549.99 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JJ823678F  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | B                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2018-04-30              |
            | Assessment Start Date     | 2017-04-30              |
            | National Insurance Number | JJ823678F               |
            | Threshold                 | 18600                   |
            | Employer Name             | Flying Pizza Ltd        |


    ##############

    Scenario: 05 Chrissy has no dependents. Her income history shows a payment within the employment check in the immediate 32 days that meets the threshold (2018-03-30). The total monthly payments in all the other months meet the annual threshold but one month is missing

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            |            |         |                |                  |
            | 2017-08-25 | 3100.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | DD827766T  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | B                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Missing payment         |
            | Application Raised date   | 2018-04-30              |
            | Assessment Start Date     | 2017-04-30              |
            | National Insurance Number | DD827766T               |
            | Threshold                 | 18600                   |
            | Employer Name             | Flying Pizza Ltd        |

    ##############

    Scenario: 06 Donald has no dependents. His income history shows a payment within the employment check in the immediate 32 days but only passes when supplemented by their partner (2018-03-30). The total monthly payments in all the other months meet the annual threshold but the last two months do not meet the monthly threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2018-03-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2018-02-25 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2018-01-30 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-12-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-11-29 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-10-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-09-25 |  775.00 | QE/Ref4        | Quality Estates  |
            | 2017-08-28 |  775.00 | QE/Ref4        | Quality Estates  |
            | 2017-07-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-06-30 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-05-29 |  775.00 | RM/Ref3        | Reliable Motors  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant                   | DD223611S  |
            | NINO - Partner                     | GG199882H  |
            | Application Raised Date            | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                |
            | Category                  | B                                                  |
            | Financial requirement met | false                                              |
            | Failure Reason            | Does not meet the threshold                        |
            | Application Raised date   | 2018-04-30                                         |
            | Assessment Start Date     | 2017-04-30                                         |
            | National Insurance Number | DD223611S                                          |
            | National Insurance Number | GG199882H                                          |
            | Threshold                 | 18600                                              |
            | Employer Name             | Flying Pizza Ltd, Reliable Motors, Quality Estates |

   ##############

    Scenario: 07 Sarah has no dependents. Her income history shows a payment within the employment check in the immediate 32 days but does not meet the threshold even when supplemented with her partner (2018-03-30). The total monthly payments in all the other months meet the monthly threshold with the minimum amounts and overall the annual threshold is met.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-01 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 |  775.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 |  774.99 | RM/Ref3        | Reliable Motors  |
            | 2018-03-27 |  775.01 | RM/Ref3        | Reliable Motors  |
            | 2018-02-25 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2018-01-30 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-12-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-11-29 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-10-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-09-25 |  775.00 | QE/Ref4        | Quality Estates  |
            | 2017-08-28 |  775.00 | QE/Ref4        | Quality Estates  |
            | 2017-07-27 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-06-30 |  775.00 | RM/Ref3        | Reliable Motors  |
            | 2017-05-29 |  775.00 | RM/Ref3        | Reliable Motors  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant                   | PK676311H  |
            | NINO - Partner                     | SD111882A  |
            | Application Raised Date            | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                |
            | Category                  | B                                                  |
            | Financial requirement met | false                                              |
            | Failure Reason            | Does not meet employment check                     |
            | Application Raised date   | 2018-04-30                                         |
            | Assessment Start Date     | 2017-04-30                                         |
            | National Insurance Number | PK676311H                                          |
            | National Insurance Number | SD111882A                                          |
            | Threshold                 | 18600                                              |
            | Employer Name             | Flying Pizza Ltd, Reliable Motors, Quality Estates |

    ##############

    Scenario: 08 Jake has one dependent. His income history shows a payment within the employment check in the immediate 32 that meets the threshold (2018-03-30). The total monthly payments in all the other months do not meet the annual threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1866.69 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PL823678H  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                            |
            | Category                  | B                              |
            | Financial requirement met | false                          |
            | Failure Reason            | Does not meet the threshold    |
            | Application Raised date   | 2018-04-30                     |
            | Assessment Start Date     | 2017-04-30                     |
            | National Insurance Number | BPL823678H                     |
            | Threshold                 | 22400                          |
            | Employer Name             | Flying Pizza Ltd               |

            ##############

    Scenario: 09 Joanne has two dependents. Her income history shows a payment within the employment check in the immediate 32 that meets the threshold (2018-03-30). The total monthly payments in all the other months do not meet the annual threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 2066.69 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PL823678H  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                            |
            | Category                  | B                              |
            | Financial requirement met | false                          |
            | Failure Reason            | Does not meet the threshold    |
            | Application Raised date   | 2018-04-30                     |
            | Assessment Start Date     | 2017-04-30                     |
            | National Insurance Number | BPL823678H                     |
            | Threshold                 | 24800                          |
            | Employer Name             | Flying Pizza Ltd               |
