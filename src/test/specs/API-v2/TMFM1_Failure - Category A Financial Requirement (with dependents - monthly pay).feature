Feature: Failure - Category A Financial Requirement - with dependents - monthly pay

    Requirement to meet Category A
    Applicant or Sponsor has received < 6 consecutive monthly payments from the same employer over the 6 month period prior to the Application Raised Date

    Financial income regulation to pass this Feature File
    Income required Amount no Dependent Child = £18600 (£1550 per month or above)
    Additional funds for 1 Dependent Child = £3800 on top of employment threshold
    Additional funds for EVERY subsequent dependent child = £2400 on top of employment threshold per child

    Financial income calculation to pass this Feature File
    Income required amount + 1 dependant amount + (Additional dependant amount * number of dependants)/12 = Gross Monthly Income is < Threshold in any one of the 6 payments in the 6 months prior to the Application Raised Date

    1 Dependent Child - £18600+£3800/12 = £1866.67
    2 Dependent Children - £18600+£3800+£2400/12 = £2066.67
    3 Dependent Children - £18600+£3800+(£2400*2)/12 = £2266.67
    4 Dependent Children - £18600+£3800+(£2400*3)/12 = £2466.67
    5 Dependent Children - £18600+£3800+(£2400*4)/12 = £2666.67
    7 Dependent Children - £18600+£3800+(£2400*6)/12 = £3066.67
    ETC

    Background: Thresholds are configured to default values
        Given The yearly threshold is configured to 18600:
        And The single dependant yearly threshold is configured to 22400:
        And The remaining dependants increment is configured to 2400:

#New scenario - Added in
    Scenario: Shelly does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    Pay date 15th of the month
    Before day of Application Raised Date
    She has 4 Canadian dependants
    She earns £2250.00 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-01-15 | 2250.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-15 | 2250.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-15 | 2250.00 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-15 | 2250.00 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-15 | 2250.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-08-15 | 2250.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - applicant        | SP123456B  |
            | Application Raised Date | 2015-02-03 |
            | Dependants              | 4          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                           |
            | Applicant                 | National Insurance Number | SP123456B                     |
            | Category A Monthly Salary | Financial requirement met | false                         |
            | Category A Monthly Salary | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Category A Monthly Salary | Assessment start date     | 2014-08-03                    |
            | Category A Monthly Salary | Application Raised date   | 2015-02-03                    |
            | Category A Monthly Salary | Threshold                 | 2466.67                       |
            | Category A Monthly Salary | Employer Name - SP123456B | Flying Pizza Ltd              |

#New scenario - Added in
    Scenario: Brian does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    Pay date 10th of the month
    On the same day of Application Raised Date
    He has 2 Thai dependants
    He earns £1416.67 Monthly Gross Income EVERY of the 6 months prior to the Application Raised Date

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-02-10 | 1416.67 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-10 | 1416.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-12-10 | 1416.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-10 | 1416.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-10-10 | 1416.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2014-09-10 | 1416.67 |             | 01           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - applicant        | BS123456B  |
            | Application Raised Date | 2015-02-10 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                           |
            | Applicant                 | National Insurance Number | BS123456B                     |
            | Category A Monthly Salary | Financial requirement met | false                         |
            | Category A Monthly Salary | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Category A Monthly Salary | Assessment start date     | 2014-08-10                    |
            | Category A Monthly Salary | Application Raised date   | 2015-02-10                    |
            | Category A Monthly Salary | Threshold                 | 2066.67                       |
            | Category A Monthly Salary | Employer Name - BS123456B | Flying Pizza Ltd              |


#New scenario - Added in SD102
    Scenario: Steve does not meet the Category A employment duration Requirement (He has worked for his current employer for only 5 months)

    Pay date 3rd of the month
    On same day of Application Raised Date
    He has 3 Thai dependants
    He earns £2916.67 Monthly Gross Income BUT for only 5 months prior to the Application Raised Date
    He worked for a different employer before his current employer (Same company under new management, i.e - different company code)

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-09-03 | 2916.67 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-03 | 2916.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 2916.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-03 | 2916.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-03 | 2916.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-04-03 | 2916.67 |             | 01           | F/Ref1         | Flying           |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - applicant        | SY987654C  |
            | Application Raised Date | 2015-09-03 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                       |
            | Applicant                 | National Insurance Number | SY987654C                 |
            | Category A Monthly Salary | Financial requirement met | false                     |
            | Category A Monthly Salary | Failure reason            | MULTIPLE_EMPLOYERS        |
            | Category A Monthly Salary | Assessment start date     | 2015-03-03                |
            | Category A Monthly Salary | Application Raised date   | 2015-09-03                |
            | Category A Monthly Salary | Threshold                 | 2266.67                   |
            | Category A Monthly Salary | Employer Name - SY987654C | Flying Pizza Ltd , FLying |

###### New scenario 31st July

    Scenario: Steve does not meet the Category A employment duration Requirement (He does not have enough records)

    Pay date 3rd of the month
    On same day of Application Raised Date
    He has 3 Thai dependants
    He earns £2916.67 Monthly Gross Income BUT for only 5 months prior to the Application Raised Date
    HMRC does not show enough records

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-09-03 | 2916.67 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-03 | 2916.67 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 2916.67 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-03 | 2916.67 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-03 | 2916.67 |             | 02           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - applicant        | SY987654C  |
            | Application Raised Date | 2015-09-03 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                |
            | Applicant                 | National Insurance Number | SY987654C          |
            | Category A Monthly Salary | Financial requirement met | false              |
            | Category A Monthly Salary | Failure reason            | NOT_ENOUGH_RECORDS |
            | Category A Monthly Salary | Assessment start date     | 2015-03-03         |
            | Category A Monthly Salary | Application Raised date   | 2015-09-03         |
            | Category A Monthly Salary | Threshold                 | 2266.67            |
            | Category A Monthly Salary | Employer Name - SY987654C | Flying Pizza Ltd   |
