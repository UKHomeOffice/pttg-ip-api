Feature: Failure - Category A Financial Requirement (with no dependents - monthly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received < 6 consecutive monthly payments from the same employer over the 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Gross Monthly Income is < £1550.00 in any one of the 6 payments in the 182 days prior to the Application Raised Date


#New Scenario -
    Scenario: Jill does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    Pay date 15th of the month
    Before day of Application Raised Date
    She earns £1000 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
            | 2015-01-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-07-15 | 1000.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JL123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-07-17                    |
            | Application Raised date   | 2015-01-15                    |
            | National Insurance Number | JL123456A                     |
            | Threshold                 | 1550.0                        |
            | Employer Name             | Flying Pizza Ltd              |

#New Scenario -
    Scenario: Francois does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    Pay date 28th of the month
    After day of Application Raised Date
    He earns £1250 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
            | 2015-03-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-02-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-01-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-07-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-06-28 | 1250.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FL123456B  |
            | Application Raised Date | 2015-03-28 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-09-27                    |
            | Application Raised date   | 2015-03-28                    |
            | National Insurance Number | FL123456B                     |
            | Threshold                 | 1550.0                        |
            | Employer Name             | Flying Pizza Ltd              |

#New Scenario -
    Scenario: Kumar does not meet the Category A employment duration Requirement (He has worked for his current employer for only 3 months)

    Pay date 3rd of the month
    On same day of Application Raised Date
    He earns £1600 Monthly Gross Income BUT for only 3 months
    He worked for a different employer before his current employer

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
            | 2015-07-03 | 1600.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-03 | 1600.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-03 | 1600.00 |            | 1           | FP/Ref1       | Flying Pizza Ltd |
        
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | KS123456C  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | NOT_ENOUGH_RECORDS |
            | Assessment start date     | 2015-01-02         |
            | Application Raised date   | 2015-07-03         |
            | National Insurance Number | KS123456C          |
            | Threshold                 | 1550.0             |
            | Employer Name             | Flying Pizza Ltd   |
