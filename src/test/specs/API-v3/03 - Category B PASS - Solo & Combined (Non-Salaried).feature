Feature: Category B Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3841 (Category B - Non-Salaried)
    # Scenarios where Category B Non-Salaried Assessments pass
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by number of months paid, multiply by 12

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800

    Scenario: No dependents. Employment check Met. Annual Check Met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 550.00  |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1050.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 3000.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 3000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

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

    ##############

    Scenario: No dependents. Employment Check Met. Annual Check Met with annualised average.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-29 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |


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

    ##############

    Scenario: No dependents. Employment check met. Annual Check Met with annualised average in immediate month.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

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

       ##############

    Scenario: No dependents. Employment check Met. Annual Check Met with payments at end of boundary.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |

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


    ##############

    Scenario: No dependents. Employment check met. Annual Check met with multiple payments in one month.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-25 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 1000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-27 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-27 | 1000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-28 | 1000.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-27 | 1000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-29 | 1000.00 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-22 | 1000.00 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-15 | 1000.00 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-08 | 1300.00 | 15          |              | FP/Ref1        | Flying Pizza Ltd |

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

    ##############

    Scenario: No dependents. Employment check met. Annual check met with multiple employers.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-22 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 50.00   |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 |             | 07           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 3000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 |             | 04           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-07-28 | 2000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 3000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                              |
            | Applicant               | National Insurance Number | AA345678A                        |
            | Category B non salaried | Financial requirement met | true                             |
            | Category B non salaried | Application Raised date   | 2018-05-23                       |
            | Category B non salaried | Assessment Start Date     | 2017-05-23                       |
            | Category B non salaried | Threshold                 | 18600                            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd, Flowers 4U Ltd |

     ##############

    Scenario: No dependents. Employment check met. Annual check met with partners income & multiple employers.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-22 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 500.00  |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 500.00  |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 500.00  |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 500.00  |             | 07           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 500.00  |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 500.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 500.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 500.00  |             | 04           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-07-28 | 500.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 500.00  |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 500.00  |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-21 | 550.00  |             | 01           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 1000.00 |             | 12           | RM/Ref3        | Reliable Motors |
            | 2018-02-25 | 1000.00 |             | 11           | RM/Ref3        | Reliable Motors |
            | 2018-01-30 | 1000.00 |             | 09           | RM/Ref3        | Reliable Motors |
            | 2017-12-27 | 1000.00 |             | 07           | RM/Ref3        | Reliable Motors |
            | 2017-11-29 | 1000.00 |             | 05           | RM/Ref3        | Reliable Motors |
            | 2017-10-27 | 1000.00 |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-09-25 | 1000.00 |             | 04           | QE/Ref4        | Quality Estates |
            | 2017-08-28 | 1000.00 |             | 04           | QE/Ref4        | Quality Estates |
            | 2017-07-27 | 1000.00 |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-06-30 | 1000.00 |             | 03           | RM/Ref3        | Reliable Motors |
            | 2017-05-29 | 1000.00 |             | 02           | RM/Ref3        | Reliable Motors |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | NINO - Partner          | BB345678B  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                              |
            | Status                  | code                      | 100                              |
            | Status                  | message                   | OK                               |
            | Applicant               | National Insurance Number | AA345678A                        |
            | Partner                 | National Insurance Number | BB345678B                        |
            | Category B non salaried | Financial requirement met | true                             |
            | Category B non salaried | Application Raised date   | 2018-05-23                       |
            | Category B non salaried | Assessment Start Date     | 2017-05-23                       |
            | Category B non salaried | NINO - Applicant          | AA345678A                        |
            | Category B non salaried | NINO - Partner            | BB345678B                        |
            | Category B non salaried | Threshold                 | 18600                            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd, Flowers 4U Ltd |
            | Category B non salaried | Employer Name - BB345678B | Reliable Motors, Quality Estates |

    ##############

    Scenario: No dependents. Employment check met with partners income. Annual check mate with partners income.

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-22 | 550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-22 | 1000.00 |             | 01           | HO/Ref3        | The Home Office |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | NINO - Partner          | BB345678B  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Partner                 | National Insurance Number | BB345678B        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-05-23       |
            | Category B non salaried | Assessment Start Date     | 2017-05-23       |
            | Category B non salaried | Threshold                 | 18600            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |
            | Category B non salaried | Employer Name - BB345678B | The Home Office  |

    ##############

    Scenario: One dependent. Employment check met. Annual check met.

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
            | 2017-05-26 | 1866.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-04-30       |
            | Category B non salaried | Assessment Start Date     | 2017-04-30       |
            | Category B non salaried | Threshold                 | 22400            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |

    ##############

    Scenario: Two dependents. Employment check met. Annual check met.

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
            | 2017-05-26 | 2066.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-04-30       |
            | Category B non salaried | Assessment Start Date     | 2017-04-30       |
            | Category B non salaried | Threshold                 | 24800            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |

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
            | 2017-05-26 | 2266.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

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

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                        |
            | Applicant               | National Insurance Number | AA345678A                  |
            | Category B non salaried | Financial requirement met | true                       |
            | Category B non salaried | Application Raised date   | 2018-04-30                 |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                 |
            | Category B non salaried | Threshold                 | 18600                      |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd, Tresco's |
