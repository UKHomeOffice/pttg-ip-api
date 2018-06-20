Feature: Category F Financial Requirement - Self-Assessment - Solo & Combined Applications

    # JIRA STORIES: EE-3842 (Self-Assessment - 1 Full Tax Year)
    # Scenarios where Self-Assessment income passes against a single full tax year
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Tax years are defined as running from 6th April to April 5th

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600 within the last full tax year
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400 within the last full tax year
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800 within the last full tax year

    Scenario: 01 Charlotte has no dependents. Her income history shows a self assessment payment in the last full tax year that meets the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 18600.00 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | F                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |

    ############

    Scenario: 02 Teresa has no dependents. Her income history shows a self assessment payment in the last full tax year that does not meet the threshold but does when combined with her partners.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2016-17 |  9300.00 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2016-17 |  9300.00 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | JH345343A  |
            | NINO - Partner          | KD722941V  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | F                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-04-06       |
            | National Insurance Number | JH345343A        |
            | National Insurance Number | KD722941V        |
            | Threshold                 | 18600            |


    ############

    Scenario: 03 Jermaine has one dependent. His income history shows a self assessment payment in the last full tax year that meets the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 22400.00 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | BB677678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | F                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | BB677678S        |
            | Threshold                 | 22400            |

    ############

    Scenario: 04 Belinda has two dependents. Her income history shows a self assessment payment in the last full tax year that meets the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 24800.00 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PP345212A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | F                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | PP345212A        |
            | Threshold                 | 24800            |
