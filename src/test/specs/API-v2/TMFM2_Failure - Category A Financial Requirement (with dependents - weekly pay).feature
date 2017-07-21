Feature: Failure - Category A Financial Requirement (with Dependantss - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received < 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial income regulation to pass this Feature File
    Income required amount no dependant child = £18600 (£1550 per month or above for EACH of the previous 6 months from the Application Raised Date)
    Additional funds for 1 dependant child = £3800 on top of employment threshold
    Additional funds for EVERY subsequent dependant child = £2400 on top of employment threshold per child

    Financial income calculation to pass this Feature File
    Income required amount + 1 dependant amount + (Additional dependant amount * number of dependants)/52 weeks in the year = 26 Weekly Gross Income payments < threshold in the 182 day period prior to the Application Raised Date from the same employer

    1 Dependant child - £18600+£3800/52 = £430.77
    2 Dependant children - £18600+£3800+£2400/12 = £476.92
    3 Dependant children - £18600+£3800+(£2400*2)/12 = £523.08
    5 Dependant children - £18600+£3800+(£2400*4)/12 = £615.38
    7 Dependant children - £18600+£3800+(£2400*6)/12 = £707.69
    ETC

#New scenario - Added in
    Scenario: Donald Sweet does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)
    Pay date - Weekly (variable dates)
    Before day of Application Raised Date
    He has 3 columbian dependants
    He has received 26 Weekly Gross Income payments of £225.40 in the 182 day period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number| PAYE Reference| Employer        |
            | 2015-10-30 | 225.40 |    1        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-23 | 225.40 |    2        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-16 | 225.40 |    3        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-09 | 225.40 |    4        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-02 | 225.40 |    5        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-25 | 225.40 |    6        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-18 | 225.40 |    7        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-11 | 225.40 |    8        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-04 | 225.40 |    9        |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-28 | 225.40 |    10       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-21 | 225.40 |    11       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-14 | 225.40 |    12       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-07 | 225.40 |    13       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-31 | 225.40 |    14       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-24 | 225.40 |    15       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-17 | 225.40 |    16       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-10 | 225.40 |    17       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-03 | 225.40 |    18       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-26 | 225.40 |    19       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-19 | 225.40 |    20       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-12 | 225.40 |    21       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-05 | 225.40 |    22       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-29 | 225.40 |    23       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-22 | 225.40 |    24       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-15 | 225.40 |    25       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-08 | 225.40 |    26       |            | FP/Ref1       | Flying Pizza Ltd |



        When the Income Proving TM v2 Family API is invoked with the following:
            | NINO                    | DS123456C  |
            | Application Raised Date | 2015-11-03 |
            | dependants              | 3          |


        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                          |
            | Financial requirement met | false                        |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Individual title          | Mr                           |
            | Individual forename       | Donald                       |
            | Individual surname        | Sweet                        |
            | Assessment start date     | 2015-05-05                   |
            | Application Raised date   | 2015-11-03                   |
            | National Insurance Number | DS123456C                    |
            | Threshold                 | 523.08                       |
            | Employer Name             | Flying Pizza Ltd             |


#New scenario - Added in SD126
    Scenario: John Lister does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    He has 2 Chinese dependants
    He has received 23 Weekly Gross Income payments of £475.67 in the 182 day period from the same employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JL123456D  |
            | Application Raised Date | 2015-01-09 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | NOT_ENOUGH_RECORDS |
            | Individual title          | Mr                 |
            | Individual forename       | John               |
            | Individual surname        | Lister             |
            | Assessment start date     | 2014-07-11         |
            | Application Raised date   | 2015-01-09         |
            | National Insurance Number | JL123456D          |
            | Threshold                 | 476.92             |
            | Employer Name             | Flying Pizza Ltd   |

#New scenario - Added in
    Scenario: Gary Goldstein does not meet the Category A employment duration Requirement (He has worked for his current employer for only 20 weeks)

    He has 3 Isreali dependants
    He has received 20 Weekly Gross Income payments of £516.67 in the 182 day period from the same employer
    He worked for a different employer before his current employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | GG987654A  |
            | Application Raised Date | 2015-09-03 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | NOT_ENOUGH_RECORDS |
            | Individual title          | Mr                 |
            | Individual forename       | Gary               |
            | Individual surname        | Goldstein          |
            | Assessment start date     | 2015-03-05         |
            | Application Raised date   | 2015-09-03         |
            | National Insurance Number | GG987654A          |
            | Threshold                 | 523.08             |
            | Employer Name             | Flying Pizza Ltd   |
