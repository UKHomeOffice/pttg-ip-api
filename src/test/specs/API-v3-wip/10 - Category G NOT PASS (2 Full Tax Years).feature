Feature: Category G Financial Requirement - Self-Assessment - Solo & Combined Applications

    # JIRA STORIES: EE-3843 (Self-Assessment - 2 Full Tax Years)
    # Scenarios where Self-Assessment income does not pass against two full tax years
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Tax years are defined as running from 6th April to April 5th

    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600 within the last full tax year
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400 within the last full tax year
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800 within the last full tax year


    Scenario: No dependents. Self-Assessment payments in the last two full tax years that do not meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 18599.98 |
            | 2016-17 | 18600.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | RF892878A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | G                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2018-04-30              |
            | National Insurance Number | RF892878A               |
            | Threshold                 | 18600                   |

         ############

    Scenario: No dependents. His income history shows a self assessment payment in the immediate tax year and in the next consecutive tax year but fails when averaged, and ignoring the third year which would make it pass.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |  9300.00 |
            | 2016-17 |  9300.00 |
            | 2015-16 | 37200.00 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | TT821878B  |
            | Application Raised Date | 2018-04-06 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | G                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2018-04-30              |
            | National Insurance Number | TT821878B               |
            | Threshold                 | 18600                   |

        ############

    Scenario: No dependents. Self-Assessment payments in the last two full tax years that do not meet the threshold and still does not even when supplemented with her partners.


        Given HMRC has the following income records
            | Date    | Amount   |
            | 2016-17 |  9300.00 |
            | 2015-16 |  9300.00 |

        And the applicants partner has the following income records:
            | Date    | Amount   |
            | 2016-17 |  9300.00 |
            | 2015-16 |  9299.99 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | AA123468A  |
            | NINO - Partner          | BB131245C  |
            | Application Raised Date | 2017-04-06 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | G                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2017-04-06              |
            | National Insurance Number | AA123468A               |
            | National Insurance Number | BB131245C               |
            | Threshold                 | 18600                   |

         ############

    Scenario: One dependent. Self-Assessment payments in the last two full tax years that do not meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 | 44799.98 |
            | 2016-17 |    00.01 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | HH273903S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | G                       |
            | Financial requirement met | false                   |
            | Failure Reason            | Does not meet threshold |
            | Application Raised date   | 2018-04-30              |
            | National Insurance Number | HH273903S               |
            | Threshold                 | 22400                   |

    ###########

    Scenario: Two dependents. Self-Assessment payments in the last two full tax years that do not meet the threshold.


        Given HMRC has the following income records:
            | Date    | Amount   |
            | 2017-18 |    00.01 |
            | 2016-18 | 49599.98 |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | LL111299C  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                     |
            | Category                  | G                       |
            | Financial requirement met | false                   |
            | Application Raised date   | 2018-04-30              |
            | Failure Reason            | Does not meet threshold |
            | National Insurance Number | LL111299C               |
            | Threshold                 | 24800                   |
