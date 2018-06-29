Feature: Category B Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3841 (Category B - Non-Salaried)
    # Scenarios where Category B Non-Salaried Assessments do not pass
    # Employment check threshold is required to be met within the immediate 32 days from and inclusive of application raised date (£1550 no dependents, £1866.70 one dependent, £2066.70 two dependents)
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by number of months paid, multiply by 12

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800

    Scenario: No dependents. Employment check not met. Annual check met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1549.99 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.01 |             | 11           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response    | HTTP Status               | 200                     |
            | Applicant        | National Insurance Number | AA345678A               |
            | Employment Check | Financial requirement met | false                   |
            | Employment Check | Failure Reason            | EMPLOYMENT_CHECK_FAILED |
            | Employment Check | Application Raised date   | 2018-04-30              |
            | Employment Check | Assessment Start Date     | 2018-03-29              |
            | Employment Check | Threshold                 | 1550.00                 |
            | Employment Check | Employer Name - AA345678A | Flying Pizza Ltd        |

    ##############

    Scenario: No dependents. Employment check met. Annual check not met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1049.99 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 3000.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-25 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-04-30                        |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried | Threshold                 | 18600                             |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |

    ##############

    Scenario: No dependents. Employment check met. Annual check not met, boundary test.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1449.99 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-29 | 00.01   |             | 01           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-04-30                        |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried | Threshold                 | 18600                             |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |

    ##############

    Scenario: No dependents. Employment check not met with multiple employers. Annual check met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 00.01   |             | 12           | FP/Ref2        | Specsavers       |
            | 2018-03-30 | 1549.98 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.01 |             | 11           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response    | HTTP Status               | 200                          |
            | Applicant        | National Insurance Number | AA345678A                    |
            | Employment Check | Financial requirement met | false                        |
            | Employment Check | Failure Reason            | EMPLOYMENT_CHECK_FAILED      |
            | Employment Check | Application Raised date   | 2018-04-30                   |
            | Employment Check | Assessment Start Date     | 2018-03-29                   |
            | Employment Check | Threshold                 | 1550.00                      |
            | Employment Check | Employer Name - AA345678A | Flying Pizza Ltd, Specsavers |

             ##############

    Scenario: No dependents. Employment check met. Annual check not met with partners income included.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-22 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 549.99  |             | 12           | FP/Ref1        | Flying Pizza Ltd |


        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-21 | 500.00 |             | 01           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 500.00 |             | 12           | RM/Ref3        | Reliable Motors |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | NINO - Partner          | BB345678A  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Partner                 | National Insurance Number | BB345678A                         |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-05-23                        |
            | Category B non salaried | Assessment Start Date     | 2017-05-23                        |
            | Category B non salaried | Threshold                 | 18600                             |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |
            | Category B non salaried | Employer Name - BB345678A | Reliable Motors                   |

        ##############

    Scenario: One dependent. Employment check met. Annual check not met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 1866.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1866.67 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1866.67 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1866.67 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1866.67 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1866.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1866.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1866.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1866.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1866.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1866.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1866.62 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-04-30                        |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried | Threshold                 | 22400                             |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |

    ##############

    Scenario: Two dependent. Employment check met. Annual check not met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 2066.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2066.67 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2066.67 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2066.67 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2066.67 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2066.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2066.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2066.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2066.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2066.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 2066.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2066.62 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-04-30                        |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried | Threshold                 | 24800                             |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |

    ##############

    Scenario: Three dependents. Employment check met. Annual check met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 2266.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2266.67 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2266.67 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2266.67 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2266.67 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2266.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2266.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2266.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2266.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2266.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 2266.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2266.63 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-04-30       |
            | Category B non salaried | Assessment Start Date     | 2017-04-30       |
            | Category B non salaried | Threshold                 | 27200            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |

    ##############

    Scenario: Seven dependents. Employment check met. Annual check met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 3066.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 3066.67 |             | 12           | FP/Ref2        | Tresco's         |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 7          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                        |
            | Applicant               | National Insurance Number | AA345678A                  |
            | Category B non salaried | Financial requirement met | true                       |
            | Category B non salaried | Application Raised date   | 2018-04-30                 |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                 |
            | Category B non salaried | Threshold                 | 36800                      |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd, Tresco's |
