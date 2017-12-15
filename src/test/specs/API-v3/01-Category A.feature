Feature: Category A Financial Requirement - Solo applicant

    # Requirement to meet Category A
    # Cat A: The income earned by the applicant during the 6 months immediately prior to the date of
    # application must total a minimum of £9,300*. The income earned each month can be a variable amount
    # The applicant must have been employed with the same employer.

    # * Annual threshold is £18,600, so evaluate 6 months and multiply by 2

    Scenario: Applicant meets category A requirements
        # PTTG-636
        # Pay dates variable
        # Amount variable
        # Employer the same

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 4000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | B                |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: Applicant does not meet requirements due to threshold
        # PTTG-636
        # Pay dates variable, some are beyond 6 months
        # Amount variable - but under threshold
        # Employer the same
        # The most recent combined payments must >= 18600/12

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 4000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-02-20 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-04-03 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | false            |
            | Failure reason            | BELOW_THRESHOLD  |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: Applicant does not meet requirements due to multiple employers
        # PTTG-636
        # Pay dates variable
        # Amount variable
        # Multiple employers

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 4000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2000.00 |             |              | HO/Ref9        | Home Office .Gov |
            | 2014-10-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | MULTIPLE_EMPLOYERS |
            | Assessment start date     | 2014-07-25         |
            | Application Raised date   | 2015-01-23         |
            | National Insurance Number | AA345678A          |
            | Threshold                 | 18600              |
            | Employer Name             | Flying Pizza Ltd

    Scenario: Applicant does not meet requirements due to recent payment restriction
    # PTTG-636
    # Pay dates variable
    # Amount variable - most recent payment is below the annual threshold/12
    # Multiple employers

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 1000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-01 | 4000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-29 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-19 | 2000.00 |             |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                    |
            | Financial requirement met | false                  |
            | Failure reason            | BELOW_RECENT_THRESHOLD |
            | Assessment start date     | 2014-07-25             |
            | Application Raised date   | 2015-01-23             |
            | National Insurance Number | AA345678A              |
            | Threshold                 | 18600                  |
            | Employer Name             | Flying Pizza Ltd       |
