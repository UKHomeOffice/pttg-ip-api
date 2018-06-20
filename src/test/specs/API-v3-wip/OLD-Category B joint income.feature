Feature: Category B Financial Requirement - Multiple employers - Joint Income

    # Requirement to meet Category B
    # The income earned by the applicant during the 12 months immediately
    # prior to the date of application must total a minimum of £18,600
    # The income earned each month can be a variable amount
    # The most recent combined payments must >= 18600/12

    Scenario: Applicant meets category B requirements with multiple employers combined with partner's
        # PTTG-634
        # Pay dates variable
        # Amount variable
        # Employer the variable
        # Joint income

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 800.00  |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 2100.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2600.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 2300.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 600.00  |             |              | HO/Ref9        | The Home Office  |
            | 2014-06-01 | 4100.00 |             |              | IK/Ref2        | Ikea             |
            | 2014-05-29 | 2600.00 |             |              | RP/RefX        | Rival Pizza Ltd  |
            | 2014-04-19 | 2300.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-23 | 700.00 |             | 06           | HO/Ref9        | The Home Office  |
            | 2014-12-23 | 700.00 |             | 05           | HO/Ref9        | The Home Office  |
            | 2014-11-23 | 700.00 |             | 04           | HO/Ref9        | The Home Office  |
            | 2014-10-23 | 700.00 |             | 03           | HO/Ref9        | The Home Office  |
            | 2014-09-23 | 700.00 |             | 02           | HO/Ref9        | The Home Office  |
            | 2014-08-23 | 700.00 |             | 01           | FP/Ref2        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |
            | Partner NINO            | BB123456A  |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                      |
            | Category                  | B                                                        |
            | Financial requirement met | true                                                     |
            | Assessment start date     | 2015-01-23                                               |
            | Application Raised date   | 2015-01-23                                               |
            | National Insurance Number | AA345678A                                                |
            | Threshold                 | 18600                                                    |
            | Employer Name             | Flying Pizza Ltd, The Home Office, Ikea, Rival Pizza Ltd |
            | Partner Employer Name     | The Home Office, Flying Pizza Ltd                        |


    Scenario: Applicant does not meet category B requirements with multiple employers even when combining income
        # PTTG-634
        # Pay dates variable
        # Employer the variable
        # Joint income

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 1500.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 1500.00 |             |              | HO/Ref9        | The Home Office  |
            | 2014-06-01 | 2000.00 |             |              | IK/Ref2        | Ikea             |
            | 2014-05-29 | 1000.00 |             |              | RP/RefX        | Rival Pizza Ltd  |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-23 | 700.00 |             | 06           | HO/Ref9        | The Home Office  |
            | 2014-12-23 | 700.00 |             | 05           | HO/Ref9        | The Home Office  |
            | 2014-11-23 | 700.00 |             | 04           | HO/Ref9        | The Home Office  |
            | 2014-10-23 | 700.00 |             | 03           | HO/Ref9        | The Home Office  |
            | 2014-09-23 | 700.00 |             | 02           | HO/Ref9        | The Home Office  |
            | 2014-08-23 | 700.00 |             | 01           | FP/Ref2        | Flying Pizza Ltd |
            | 2014-07-23 | 700.00 |             | 12           | FP/Ref2        | Flying Pizza Ltd |
            | 2014-06-23 | 700.00 |             | 11           | FP/Ref2        | Flying Pizza Ltd |
            | 2014-05-23 | 700.00 |             | 10           | FP/Ref2        | Flying Pizza Ltd |
            | 2014-04-19 | 700.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-03-23 | 700.00 |             | 08           | FP/Ref2        | Flying Pizza Ltd |
            | 2014-02-23 | 700.00 |             | 07           | FP/Ref2        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |
            | Partner NINO            | BB123456A  |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                                      |
            | Financial requirement met | false                                                    |
            | Failure reason            | BELOW_THRESHOLD                                          |
            | Assessment start date     | 2015-01-23                                               |
            | Application Raised date   | 2015-01-23                                               |
            | National Insurance Number | AA345678A                                                |
            | Threshold                 | 18600                                                    |
            | Employer Name             | Flying Pizza Ltd, The Home Office, Ikea, Rival Pizza Ltd |
            | Partner Employer Name     | The Home Office, Flying Pizza Ltd                        |

