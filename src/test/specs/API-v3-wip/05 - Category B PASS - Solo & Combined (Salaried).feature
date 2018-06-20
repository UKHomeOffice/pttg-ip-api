Feature: Category B Financial Requirement - Solo & Combined Applications for Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3840 (Category B - Salaried)
    # Scenarios where Category B Salaried Assessments pass
    # Employment check threshold is required to be met within the immediate 32 days from and inclusive of application raised date (£1550 no dependents, £1866.70 one dependent, £2066.70 two dependents)
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total of all payments within each consecutive month within the 12-month range

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600. Monthly threshold is set at £1550
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400. Monthly threshold is set at £1866.70
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800. Monthly threshold is set at £2066.70

    ##############

    Scenario: 01 Celina has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts

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
            | 2017-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | BF985678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | BF985678S        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

    ##############

    Scenario: 02 Robert has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days at the earliest opportunity (2018-03-30) and payments in all 12 months that meet the monthly threshold with varying amounts

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-01-26 | 2400.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | QX846678T  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | QX846678T        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

        ##############

    Scenario: 03 Charlotte has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and payments in all 12 months that meet the monthly threshold with varying amounts and frequencies

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-01-26 | 2400.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-27 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-20 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-13 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-06 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | QX846678T  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | QX846678T        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    ##############

    Scenario: 04 Crystal has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days made up of weekly frequencies (2018-03-30) with all other months meeting the threshold with variable amounts.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 |  625.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-20 |  387.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-13 |  387.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-06 |  150.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-01-26 | 2400.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | DG894578V  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | DG894578V        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

       ##############

    Scenario: 05 Kelly has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days with monthly and weekly frequencies (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1012.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-13 |  387.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-06 |  150.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-01-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | TH667678P  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | TH667678P        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

      ##############

    Scenario: 06 Frederick has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts across multiple employers.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 | 1550.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | KX876678P  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | KX876678P        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

              ##############

    Scenario: 07 Sally has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts across two income streams.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 |  775.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-21 |   00.00 | RM/Ref3        | Reliable Motors  |
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
            | NINO - Applicant        | TY383678P  |
            | NINO - Partner          | GB836410O  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                                               |
            | Category                  | B                                                                                 |
            | Financial requirement met | true                                                                              |
            | Application Raised date   | 2018-04-30                                                                        |
            | Assessment Start Date     | 2017-04-30                                                                        |
            | National Insurance Number | TY383678P                                                                         |
            | National Insurance Number | GB836410O                                                                         |
            | Threshold                 | 18600                                                                             |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos, Reliable Motors, Quality Estates |

    ##############

    Scenario: 08 Sally has no dependents. Her income history does not shows a payment that meets the employment check in the immediate 32 days (2018-03-30) but her partner does. All the other months meeting the monthly threshold with the minimum amounts across two income streams.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 |   00.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 |  775.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-21 | 1550.00 | RM/Ref3        | Reliable Motors  |
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
            | NINO - Applicant        | TY383678P  |
            | NINO - Partner          | GB836410O  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                                               |
            | Category                  | B                                                                                 |
            | Financial requirement met | true                                                                              |
            | Application Raised date   | 2018-04-30                                                                        |
            | Assessment Start Date     | 2017-04-30                                                                        |
            | National Insurance Number | TY383678P                                                                         |
            | National Insurance Number | GB836410O                                                                         |
            | Threshold                 | 18600                                                                             |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos, Reliable Motors, Quality Estates |

    ##############

    Scenario: 09 Sally has no dependents. Her income history shows a payment that doesn't meet the employment check in the immediate 32 days (2018-03-30) but does when combined with her partners income. All the other months meeting the monthly threshold with the minimum amounts across two income streams.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-30 |  775.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 |  775.00 | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 |  775.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  775.00 | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 |  775.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-21 |  775.00 | RM/Ref3        | Reliable Motors  |
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
            | NINO - Applicant        | TY383678P  |
            | NINO - Partner          | GB836410O  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                                               |
            | Category                  | B                                                                                 |
            | Financial requirement met | true                                                                              |
            | Application Raised date   | 2018-04-30                                                                        |
            | Assessment Start Date     | 2017-04-30                                                                        |
            | National Insurance Number | TY383678P                                                                         |
            | National Insurance Number | GB836410O                                                                         |
            | Threshold                 | 18600                                                                             |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos, Reliable Motors, Quality Estates |

    ##############

    Scenario: 10 Ryan has one dependent. His income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts

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
            | 2017-04-30 | 1866.70 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AS995678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | AS995678S        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

    ##############

    Scenario: 11 Florence has two dependents. Her income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts

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
            | 2017-04-30 | 2066.70 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | WA888878S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | Assessment Start Date     | 2017-04-30       |
            | National Insurance Number | WA888878S        |
            | Threshold                 | 24800            |
            | Employer Name             | Flying Pizza Ltd |
