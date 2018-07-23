Feature: Category G Financial Requirement - Self-Assessment - Solo & Combined Applications

    # JIRA STORIES: EE-3843 (Self-Assessment - 2 Full Tax Years)
    # Scenarios where Self-Assessment income passes against two full tax years
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Tax years are defined as running from 6th April to April 5th
    # The mean average will be used to compare against the below thresholds. The formula is: Total of all yearly payments divided by 2

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600 within the last full tax year
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400 within the last full tax year
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800 within the last full tax year


    Scenario: No dependents. Self-Assessment payments in the last two full tax years that meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 18599.99 |
            | 2016-17 | 18600.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | BB388878A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | BB388878A        |
            | Threshold                 | 18600            |

    ############

    Scenario: No dependents. Self-Assessment payments in the last two full tax years that do not meet the threshold but does when supplemented with his partners.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |  9300.00 |
            | 2016-17 |  9299.99 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2017-18 |  9300.00 |
            | 2016-17 |  9300.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | KK928278A  |
            | NINO - Partner          | PP666941A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | KK928278A        |
            | National Insurance Number | PP666941A        |
            | Threshold                 | 18600            |

    ############

    Scenario: One dependent. Self-Assessment payments in the last two full tax years that meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |    00.01 |
            | 2016-17 | 47999.99 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JH655678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | JH655678S        |
            | Threshold                 | 22400            |

    ############

    Scenario: Two dependents. Self-Assessment payments in the last two full tax years that meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 49599.99 |
            | 2016-17 |    00.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | GH999872A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | GH999872A        |
            | Threshold                 | 24800            |

    ############

    Scenario: No dependents. No Self-Assessment payments in the last two full tax years but payments in the partners.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |    00.00 |
            | 2016-17 |    00.00 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2017-18 | 18599.99 |
            | 2016-17 | 18600.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | KK928278A  |
            | NINO - Partner          | PP666941A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | KK928278A        |
            | National Insurance Number | PP666941A        |
            | Threshold                 | 18600            |

    ############

    Scenario: No dependents. No Self-Assessment payments in the last two full tax years but payments in the partners.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |    00.00 |
            | 2016-17 |    00.00 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2017-18 | 18599.99 |
            | 2016-17 | 18600.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | KK928278A  |
            | NINO - Partner          | PP666941A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | KK928278A        |
            | National Insurance Number | PP666941A        |
            | Threshold                 | 18600            |

    ############

    Scenario: No dependents. No Self-Assessment payments in the last two full tax years but payments in the partners.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |    00.00 |
            | 2016-17 |  9299.99 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2017-18 |  9299.99 |
            | 2016-17 | 18600.02 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | KK928278A  |
            | NINO - Partner          | PP666941A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | G                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | KK928278A        |
            | National Insurance Number | PP666941A        |
            | Threshold                 | 18600            |
