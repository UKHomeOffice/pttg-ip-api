Feature: Category B Financial Requirement - Mulitple employers - Solo applicant

    # Requirement to meet Category B
    # The income earned by the applicant during the 12 months immediately
    # prior to the date of application must total a minimum of £18,600
    # The income earned each month can be a variable amount
    # The most recent combined payments must >= 18600/12

    Scenario: Applicant meets category B requirements with multiple employers
        # PTTG-636
        # Pay dates variable
        # Amount variable
        # Employer the variable

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 600.00  |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 4100.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2600.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 2300.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 600.00  |             |              | HO/Ref9        | The Home Office  |
            | 2014-06-01 | 4100.00 |             |              | IK/Ref2        | Ikea             |
            | 2014-05-29 | 2600.00 |             |              | RP/RefX        | Rival Pizza Ltd  |
            | 2014-04-19 | 2300.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                      |
            | Category                  | B                                                        |
            | Financial requirement met | true                                                     |
            | Assessment start date     | 2015-01-23                                               |
            | Application Raised date   | 2015-01-23                                               |
            | National Insurance Number | AA345678A                                                |
            | Threshold                 | 18600                                                    |
            | Employer Name             | Flying Pizza Ltd, The Home Office, Ikea, Rival Pizza Ltd |


    Scenario: Applicant does not meet category B requirements with multiple employers
        # PTTG-636
        # Pay dates variable
        # Amounts variable
        # Employer the variable

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 1000.00 |             |              | HO/Ref9        | The Home Office  |
            | 2014-06-01 | 1000.00 |             |              | IK/Ref2        | Ikea             |
            | 2014-05-29 | 2000.00 |             |              | RP/RefX        | Rival Pizza Ltd  |
            | 2014-04-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-03-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-02-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                      |
            | Failure reason            | BELOW_THRESHOLD                                          |
            | Financial requirement met | false                                                    |
            | Assessment start date     | 2015-01-23                                               |
            | Application Raised date   | 2015-01-23                                               |
            | National Insurance Number | AA345678A                                                |
            | Threshold                 | 18600                                                    |
            | Employer Name             | Flying Pizza Ltd, The Home Office, Ikea, Rival Pizza Ltd |

