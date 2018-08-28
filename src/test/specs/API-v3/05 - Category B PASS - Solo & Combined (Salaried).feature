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

    # 01 Celina has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts
    Scenario: No Dependents. Employment check met. 12 monthly payments over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
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
            | NINO - Applicant        | BR985678B  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | BR985678B        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - BR985678B | Flying Pizza Ltd |

    ##############

    # 02 Robert has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days at the earliest opportunity (2018-03-30) and payments in all 12 months that meet the monthly threshold with varying amounts
    Scenario:  No dependents. Employment check met. 12 variable monthly payments over threshold

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-26 | 2400.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TX846678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | TX846678A        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - TX846678A | Flying Pizza Ltd |

    ##############

    # 03 Charlotte has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and payments in all 12 months that meet the monthly threshold with varying amounts and frequencies
    Scenario: No dependents. Employment check met. Payments of variable amount and frequency but over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-26 | 2400.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-27 | 500.00  | 27          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-20 | 500.00  | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-13 | 500.00  | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-06 | 500.00  | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TX846678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | TX846678A        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - TX846678A | Flying Pizza Ltd |

    ##############

    # 04 Crystal has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days made up of weekly frequencies (2018-03-30) with all other months meeting the threshold with variable amounts.
    Scenario: No dependents. Employment check met by weekly payments. Other months over threshold but variable amounts.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 625.00  | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-20 | 387.50  | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-13 | 387.50  | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            # TODO OJR: Get the below checked - we don't have enough payments for April if it's left unchanged?
            | 2018-04-06 | 150.00  | 49          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 2600.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2500.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-26 | 2400.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2300.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 2200.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 2100.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1900.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1800.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1700.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1600.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | EG894578C  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | EG894578C        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - EG894578C | Flying Pizza Ltd |

       ##############

    # 05 Kelly has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days with monthly and weekly frequencies (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts.
    Scenario: No dependents. Monthly and weekly payments to meet employment check.  All other months just over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1012.50 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-04-13 | 387.50  | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            # TODO OJR: Get the below checked - we don't have enough payments for April if it's left unchanged?
            | 2018-04-06 | 150.00  | 49          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1550.00 | 52          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-26 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TH667678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | TH667678A        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - TH667678A | Flying Pizza Ltd |

    ##############

    # 06 Frederick has no dependents. His income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts across multiple employers.
    Scenario: No dependents. Employment check met. Monthly payments just over threshold but multiple employers.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 01           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1550.00 |             | 09           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1550.00 |             | 05           | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1550.00 |             | 03           | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | KX876678B  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                                             |
            | Applicant           | National Insurance Number | KX876678B                                       |
            | Category B salaried | Financial requirement met | true                                            |
            | Category B salaried | Application Raised date   | 2018-04-30                                      |
            | Category B salaried | Assessment Start Date     | 2017-04-30                                      |
            | Category B salaried | Threshold                 | 18600                                           |
            | Category B salaried | Employer Name - KX876678B | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos |

    ##############

    # 07 Sally has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days (2018-03-30) and all the other months meeting the monthly threshold with the minimum amounts across two income streams.
    Scenario: No dependents. Employment check met. Monthly payments from multiple employers over threshold when combined with partner.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 1550.00 |             | 01           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 | 775.00  |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 775.00  |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 775.00  |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 775.00  |             | 09           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 775.00  |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 775.00  |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 775.00  |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 775.00  |             | 05           | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 | 775.00  |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 775.00  |             | 03           | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 | 775.00  |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-21 | 00.00  |             | 01           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 775.00 |             | 12           | RM/Ref3        | Reliable Motors |
            | 2018-02-25 | 775.00 |             | 11           | RM/Ref3        | Reliable Motors |
            | 2018-01-30 | 775.00 |             | 10           | RM/Ref3        | Reliable Motors |
            | 2017-12-27 | 775.00 |             | 09           | RM/Ref3        | Reliable Motors |
            | 2017-11-29 | 775.00 |             | 08           | RM/Ref3        | Reliable Motors |
            | 2017-10-27 | 775.00 |             | 07           | RM/Ref3        | Reliable Motors |
            | 2017-09-25 | 775.00 |             | 06           | QE/Ref4        | Quality Estates |
            | 2017-08-28 | 775.00 |             | 05           | QE/Ref4        | Quality Estates |
            | 2017-07-27 | 775.00 |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-06-30 | 775.00 |             | 03           | RM/Ref3        | Reliable Motors |
            | 2017-05-29 | 775.00 |             | 02           | RM/Ref3        | Reliable Motors |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TY383678C  |
            | NINO - Partner          | GE836410D  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                                             |
            | Applicant           | National Insurance Number | TY383678C                                       |
            | Partner             | National Insurance Number | GE836410D                                       |
            | Category B salaried | Financial requirement met | true                                            |
            | Category B salaried | Application Raised date   | 2018-04-30                                      |
            | Category B salaried | Assessment Start Date     | 2017-04-30                                      |
            | Category B salaried | Threshold                 | 18600                                           |
            | Category B salaried | Employer Name - TY383678C | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos |
            | Category B salaried | Employer Name - GE836410D | Reliable Motors, Quality Estates                |

    ##############

    # 08 Sally has no dependents. Her income history does not shows a payment that meets the employment check in the immediate 32 days (2018-03-30) but her partner does. All the other months meeting the monthly threshold with the minimum amounts across two income streams.
    Scenario: No dependents. Employment check met but not met by partner. Monthly payments from multiple employers over threshold when combined with partner.

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 00.00  |             | 01           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 | 775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 775.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 775.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 775.00 |             | 09           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 775.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 775.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 775.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 775.00 |             | 05           | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 | 775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 775.00 |             | 03           | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 | 775.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-21 | 1550.00 |             | 01           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 775.00  |             | 12           | RM/Ref3        | Reliable Motors |
            | 2018-02-25 | 775.00  |             | 11           | RM/Ref3        | Reliable Motors |
            | 2018-01-30 | 775.00  |             | 10           | RM/Ref3        | Reliable Motors |
            | 2017-12-27 | 775.00  |             | 09           | RM/Ref3        | Reliable Motors |
            | 2017-11-29 | 775.00  |             | 08           | RM/Ref3        | Reliable Motors |
            | 2017-10-27 | 775.00  |             | 07           | RM/Ref3        | Reliable Motors |
            | 2017-09-25 | 775.00  |             | 06           | QE/Ref4        | Quality Estates |
            | 2017-08-28 | 775.00  |             | 05           | QE/Ref4        | Quality Estates |
            | 2017-07-27 | 775.00  |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-06-30 | 775.00  |             | 03           | RM/Ref3        | Reliable Motors |
            | 2017-05-29 | 775.00  |             | 02           | RM/Ref3        | Reliable Motors |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TY383678C  |
            | NINO - Partner          | GE836410D  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                                              |
            | Applicant           | National Insurance Number | TY383678C                                        |
            | Partner             | National Insurance Number | GE836410D                                        |
            | Category B salaried | Financial requirement met | true                                             |
            | Category B salaried | Application Raised date   | 2018-04-30                                       |
            | Category B salaried | Assessment Start Date     | 2017-04-30                                       |
            | Category B salaried | Threshold                 | 18600                                            |
            | Category B salaried | Employer Name - TY383678C | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos, |
            | Category B salaried | Employer Name - GE836410D | Reliable Motors, Quality Estates                 |

    ##############

    # 09 Sally has no dependents. Her income history shows a payment that doesn't meet the employment check in the immediate 32 days (2018-03-30) but does when combined with her partners income. All the other months meeting the monthly threshold with the minimum amounts across two income streams.
    Scenario: No dependents. Employment check only met when combined with partner.  Monthly payments over threshold when combined with partner.

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 775.00 |             | 01           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2018-03-30 | 775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 775.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 775.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 775.00 |             | 07           | F4U/Ref2       | Flowers 4U Ltd   |
            | 2017-11-30 | 775.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 775.00 |             | 04           | SA/Ref3        | Steve's Autos    |
            | 2017-07-28 | 775.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 775.00 |             | 03           | SA/Ref3        | Steve's Autos    |
            | 2017-05-26 | 775.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-21 | 775.00 |             | 01           | RM/Ref3        | Reliable Motors |
            | 2018-03-27 | 775.00 |             | 12           | RM/Ref3        | Reliable Motors |
            | 2018-02-25 | 775.00 |             | 11           | RM/Ref3        | Reliable Motors |
            | 2018-01-30 | 775.00 |             | 09           | RM/Ref3        | Reliable Motors |
            | 2017-12-27 | 775.00 |             | 07           | RM/Ref3        | Reliable Motors |
            | 2017-11-29 | 775.00 |             | 05           | RM/Ref3        | Reliable Motors |
            | 2017-10-27 | 775.00 |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-09-25 | 775.00 |             | 04           | QE/Ref4        | Quality Estates |
            | 2017-08-28 | 775.00 |             | 04           | QE/Ref4        | Quality Estates |
            | 2017-07-27 | 775.00 |             | 04           | RM/Ref3        | Reliable Motors |
            | 2017-06-30 | 775.00 |             | 03           | RM/Ref3        | Reliable Motors |
            | 2017-05-29 | 775.00 |             | 02           | RM/Ref3        | Reliable Motors |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | TY383678C  |
            | NINO - Partner          | GE836410D  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200                                             |
            | Applicant           | National Insurance Number | TY383678C                                       |
            | Partner             | National Insurance Number | GE836410D                                       |
            | Category B salaried | Financial requirement met | true                                            |
            | Category B salaried | Application Raised date   | 2018-04-30                                      |
            | Category B salaried | Assessment Start Date     | 2017-04-30                                      |
            | Category B salaried | Threshold                 | 18600                                           |
            | Category B salaried | Employer Name - TY383678C | Flying Pizza Ltd, Flowers 4U Ltd, Steve's Autos |
            | Category B salaried | Employer Name - GE836410D | Reliable Motors, Quality Estates                |

    ##############

    # 10 Ryan has one dependent. His income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts
    Scenario: One dependent. Employment check met. Payments over threshold for all 12 previous months.

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
            | 2017-04-30 | 1866.70 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AS995678B  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | AS995678B        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 22400            |
            | Category B salaried | Employer Name - AS995678B | Flying Pizza Ltd |

    ##############

    # 11 Florence has two dependents. Her income history shows a payment that meets the employment check in the immediate 32 days on the last possible day (2018-03-30) and payments in all the 12 months prior that meet the monthly threshold with the minimum amounts
    Scenario: Two dependents. Employment check met on last possible day. Payments over threshold for all 12 previous months.

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
            | 2017-04-30 | 2066.70 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | WA888878C  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | WA888878C        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 24800            |
            | Category B salaried | Employer Name - WA888878C | Flying Pizza Ltd |

##############

    # 12 Category B. No dependents. Annual Check Met. Multiple payments in the month at the end of the assessment range. (To Test Adding of multiple payments in a month)
    Scenario: No dependents. Employment check met. Multiple payments on last day of month.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 1550.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-30 | 1550.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-28 | 1550.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-31 | 1550.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-29 | 1550.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 1550.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-27 | 1550.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-30 | 1550.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1550.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-31 | 1550.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-29 | 1550.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 0.01    |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 1549.99 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response       | HTTP Status               | 200              |
            | Applicant           | National Insurance Number | AA345678A        |
            | Category B salaried | Financial requirement met | true             |
            | Category B salaried | Application Raised date   | 2018-04-30       |
            | Category B salaried | Assessment Start Date     | 2017-04-30       |
            | Category B salaried | Threshold                 | 18600            |
            | Category B salaried | Employer Name - AA345678A | Flying Pizza Ltd |
