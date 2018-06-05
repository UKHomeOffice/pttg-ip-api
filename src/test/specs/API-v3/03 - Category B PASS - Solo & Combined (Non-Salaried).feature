Feature: Category B Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3841 (Category B - Non-Salaried)
    # Scenarios where Category B Non-Salaried Assessments pass
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by number of months paid, multiply by 12

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800

    Scenario: 01 Georgina has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and payments in all the 12 months prior that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 |  550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1050.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | GH345678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30
            | National Insurance Number | GH345678S        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

    ##############

    Scenario: 02 Carl has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and payments in 1 proceeding month prior that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FB857678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | FB857678S        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

    ##############

    Scenario: 03 Belinda has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) with no other payments.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FK854578V  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | FK854578V        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

       ##############

    Scenario: 04 Josephine has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) with one other payments at the end of the range.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FK854578V  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | FK854578V        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    ##############

    Scenario: 05 Cecil has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) with 6 months where some are made up of multiple frequencies.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-28 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-25 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-25 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-25 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-25 | 1300.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | CE854578T  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | CE854578T        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

    ##############

    Scenario: 06 Silvia has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-04-22) and payments in all the 12 months with multiple employers that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-22 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 |   50.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-07-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AS345008S  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                              |
            | Category                  | B                                |
            | Financial requirement met | true                             |
            | Application Raised date   | 2018-05-23                       |
            | Assessment Start Date     | 2017-05-23                       |
            | National Insurance Number | AS345008S                        |
            | Threshold                 | 18600                            |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd |

     ##############

    Scenario: 07 Jeremy has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2018-04-22) but needs a partners income to supplement his own to meet the threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-22 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  500.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  500.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-07-28 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 |  500.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-21 |  550.00 | RM/Ref3        | Reliable Motors  |
            | 2018-03-27 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2018-02-25 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2018-01-30 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-12-27 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-11-29 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-10-27 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-09-25 | 1000.00 | QE/Ref4        | Quality Estates  |
            | 2017-08-28 | 1000.00 | QE/Ref4        | Quality Estates  |
            | 2017-07-27 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-06-30 | 1000.00 | RM/Ref3        | Reliable Motors  |
            | 2017-05-29 | 1000.00 | RM/Ref3        | Reliable Motors |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | WE345065B  |
            | NINO - Partner          | HJ372689H  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                                |
            | Category                  | B                                                                  |
            | Financial requirement met | true                                                               |
            | Application Raised date   | 2018-05-23                                                         |
            | Assessment Start Date     | 2017-05-23                                                         |
            | National Insurance Number | WE345065B                                                          |
            | National Insurance Number | HJ372689H                                                          |
            | Threshold                 | 18600                                                              |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd, Reliable Motors, Quality Estates |

    ##############

    Scenario: 08 Jeremy has no dependents. His income history shows a payment that doesn't meet the employment check in the immediate 32 days (2018-04-22) but needs a partners income to meet the employment check and supplement his own to meet the threshold regardless of gaps

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-21 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  500.00 | F4U/Ref2       | Flowers 4U Ltd   |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-22 | 1000.00 | HO/Ref3        | The Home Office  |
            | 2018-03-27 | 1000.00 | HO/Ref3        | The Home Office  |
            | 2017-11-30 |  162.50 | HO/Ref3        | The Home Office  |
            | 2017-11-24 |  162.50 | HO/Ref3        | The Home Office  |
            | 2017-11-10 |  162.50 | HO/Ref3        | The Home Office  |
            | 2017-11-03 |  162.50 | HO/Ref3        | The Home Office  |
            | 2017-08-28 | 1000.00 | PP/Ref4        | Pete's Pasties   |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | TR342965Q  |
            | NINO - Partner          | PO399989H  |
            | Application Raised Date | 2018-05-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                               |
            | Category                  | B                                                                 |
            | Financial requirement met | true                                                              |
            | Application Raised date   | 2018-05-23                                                        |
            | Assessment Start Date     | 2017-05-23                                                        |
            | National Insurance Number | TR342965Q                                                         |
            | National Insurance Number | PO399989H                                                         |
            | Threshold                 | 18600                                                             |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd, The Home Office, Pete's Pasties |

    ##############

    Scenario: 09 Lucy has one dependent. Her income history shows a payment that meets the employment check in the immediate 32 days (2017-11-09) and payments in all the 12 months prior that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-11-09 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1866.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2183.30 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | DF765678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-10       |
            | Assessment Start Date     | 2016-12-10       |
            | National Insurance Number | DF765678S        |
            | Threshold                 | 22400            |
            | Employer Name             | Flying Pizza Ltd |

            ##############

    Scenario: 10 Bernard has two dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2017-11-09) and payments in all the 12 months prior that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-11-09 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 2066.67 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 2583.30 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | DF765678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-10       |
            | Assessment Start Date     | 2016-12-10       |
            | National Insurance Number | DF765678S        |
            | Threshold                 | 24800            |
            | Employer Name             | Flying Pizza Ltd |
