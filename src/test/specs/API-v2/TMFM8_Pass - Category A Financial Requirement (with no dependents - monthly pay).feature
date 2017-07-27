Feature: Pass - Category A Financial Requirement (with no dependents - monthly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received 6 consecutive monthly payments from the same employer over the 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has earned 6 monthly payments => £1550 Monthly Gross Income in the 182 days prior to the Application Raised Date

#New Scenario -
    Scenario: Jon meets the Category A Financial Requirement (1)

    Pay date 15th of the month
    Before day of Application Raised Date
    He earns £1600 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2015-01-15 | 1600.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-15 | 1600.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-15 | 1600.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-15 | 1600.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-15 | 1600.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-15 | 1600.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |

#New Scenario -
    Scenario: Jon meets the Category A Financial Requirement (Caseworker enters the National Insurance Number with spaces)

    Pay date 1st of the month
    Before day of Application Raised Date
    He earns £1550 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2014-12-01 | 1550.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-01 | 1550.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-01 | 1550.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-01 | 1550.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-01 | 1550.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-07-01 | 1550.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA 12 34 56 B |
            | Application Raised Date | 2015-01-10    |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-12       |
            | Application Raised date   | 2015-01-10       |
            | National Insurance Number | AA123456B        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Jon meets the Category A Financial Requirement (2)

    Pay date 28th of the month
    After day of Application Raised Date
    He earns £2240 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2014-12-28 | 2240.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-28 | 2240.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-28 | 2240.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-28 | 2240.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-28 | 2240.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-07-28 | 2240.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | BB123456B  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | BB123456B        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Jon meets the Category A Financial Requirement (3)

    Pay date 23rd of the month
    On same day of Application Raised Date
    He earns £1551 Monthly Gross Income EVERY of the 6

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2015-01-23 | 1551.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-23 | 1551.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-23 | 1551.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-23 | 1551.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-23 | 1551.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-23 | 1551.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | CC123456C  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | CC123456C        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Jon meets the Category A Financial Requirement (Application Raised Date provided with single numbers for the day and month)

    Pay date 1st of the month
    After day of Application Raised Date
    He earns £3210 Monthly Gross Income EVERY of the 6

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2015-01-01 | 3210.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-01 | 3210.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-01 | 3210.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-01 | 3210.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-01 | 3210.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-01 | 3210.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | CC123456B  |
            | Application Raised Date | 2015-01-09 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-11       |
            | Application Raised date   | 2015-01-09       |
            | National Insurance Number | CC123456B        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |

#New Scenario -
    Scenario: Mark meets the Category A Financial Requirement

    Pay date 17th, for December 2014
    Pay date 30th, for October 2014
    Pay date 15th for all other months
    On different day of Application Raised Date
    He earns £1600 Monthly Gross Income EVERY of the 6

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 1600.00 |            | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-17 | 1600.00 |            | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-15 | 1600.00 |            | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-30 | 1600.00 |            | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 1600.00 |            | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-15 | 1600.00 |            | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA123456A  |
            | Application Raised Date | 2015-01-23 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA123456A        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |


    ###### New scenario - 27th July

    Scenario: Jon meets the Category A Financial Requirement and has a fluctuating income(Caseworker enters the National Insurance Number with spaces)

    Pay date 1st of the month
    Before day of Application Raised Date
    He earns £1550 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference | Employer         |
            | 2014-12-01 | 1550.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-01 | 1650.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-01 | 1550.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-01 | 1950.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-01 | 1550.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-07-01 | 1750.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA 12 34 56 B |
            | Application Raised Date | 2015-01-10    |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-12       |
            | Application Raised date   | 2015-01-10       |
            | National Insurance Number | AA123456B        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |
