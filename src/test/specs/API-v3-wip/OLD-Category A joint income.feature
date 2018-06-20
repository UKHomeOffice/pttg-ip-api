Feature: Category A Financial Requirement - Joint income

    # Requirement to meet Category A
    # Applicant and partner between them have received 6 consecutive monthly payments from the same
    # employer each over the 6 month period prior to the Application Raised Date
    # The combined threshold is £9,300 over the 6 months
    # The most recent combined payments must >= 18600/12

    Scenario: Applicant and partner meet the Category A Financial Requirement by combining income
        # PTTG-634
        # Pay dates can be variable
        # Applicant earns £1000 Monthly Gross Income EVERY of the 6 months
        # Partner earns £600 per month

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 1000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-26 | 1000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-16 | 1000.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-11 | 1000.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 1000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-15 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2015-01-23 | 600.00 |             | 06           | HO/Ref9        | The Home Office |
            | 2014-12-23 | 600.00 |             | 05           | HO/Ref9        | The Home Office |
            | 2014-11-22 | 600.00 |             | 04           | HO/Ref9        | The Home Office |
            | 2014-10-23 | 600.00 |             | 03           | HO/Ref9        | The Home Office |
            | 2014-09-25 | 600.00 |             | 02           | HO/Ref9        | The Home Office |
            | 2014-08-23 | 600.00 |             | 01           | HO/Ref9        | The Home Office |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |
            | Partner NINO            | BB123456A  |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |
            | Partner Employer Name     | The Home Office  |


    Scenario: Applicant and partner do not meet the Category A Financial Requirement by combining income
        # PTTG-634
        # Pay dates can be variable
        # Applicant earns £900 Monthly Gross Income EVERY of the 6 months
        # Partner earns £600 per month

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 900.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-15 | 900.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-15 | 900.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-15 | 900.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 900.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-15 | 900.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer        |
            | 2015-01-23 | 600.00 |             | 06           | HO/Ref9        | The Home Office |
            | 2014-12-23 | 600.00 |             | 05           | HO/Ref9        | The Home Office |
            | 2014-11-23 | 600.00 |             | 04           | HO/Ref9        | The Home Office |
            | 2014-10-23 | 600.00 |             | 03           | HO/Ref9        | The Home Office |
            | 2014-09-23 | 600.00 |             | 02           | HO/Ref9        | The Home Office |
            | 2014-08-23 | 600.00 |             | 01           | HO/Ref9        | The Home Office |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |
            | Partner NINO            | BB123456A  |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | false            |
            | Failure reason            | BELOW_THRESHOLD  |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 1550.0           |
            | Employer Name             | Flying Pizza Ltd |
            | Partner Employer Name     | The Home Office  |

    Scenario: Applicant and partner do not meet the Category A Financial Requirement by combining income - one of them has more than one employer
        # PTTG-634
        # Pay dates can be variable
        # Applicant earns £900 Monthly Gross Income EVERY of the 6 months
        # Partner earns £700 per month

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 900.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-22 | 900.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-15 | 900.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-15 | 900.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-17 | 900.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-15 | 900.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-23 | 700.00 |             | 06           | HO/Ref9        | The Home Office  |
            | 2014-12-23 | 700.00 |             | 05           | HO/Ref9        | The Home Office  |
            | 2014-11-28 | 700.00 |             | 04           | HO/Ref9        | The Home Office  |
            | 2014-10-23 | 700.00 |             | 03           | HO/Ref9        | The Home Office  |
            | 2014-09-01 | 700.00 |             | 02           | HO/Ref9        | The Home Office  |
            | 2014-08-23 | 700.00 |             | 01           | FP/Ref2        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2015-01-23 |
            | Partner NINO            | BB123456A  |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                               |
            | Financial requirement met | false                             |
            | Failure reason            | MULTIPLE_EMPLOYERS                |
            | Assessment start date     | 2014-07-25                        |
            | Application Raised date   | 2015-01-23                        |
            | National Insurance Number | AA345678A                         |
            | Threshold                 | 1550.0                            |
            | Employer Name             | Flying Pizza Ltd                  |
            | Partner Employer Name     | The Home Office, Flying Pizza Ltd |
