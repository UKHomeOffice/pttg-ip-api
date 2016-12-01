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
    Scenario: Donald Sweet does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    He has 3 columbian dependants
    He has received 26 Weekly Gross Income payments of £225.40 in the 182 day period from the same employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | DS123456C  |
            | Application Raised Date | 2015-11-03 |
            | Dependant               | 3          |


        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                          |
            | Financial requirement met  | false                        |
            | Failure reason             | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Individual title           | Mr                           |
            | Individual forename        | Donald                       |
            | Individual surname         | Sweet                        |
            | Application Raised to date | 2015-05-05                   |
            | Application Raised date    | 2015-11-03                   |
            | National Insurance Number  | DS123456C                    |
            | Threshold                  | 523.08                       |


#New scenario - Added in SD126
    Scenario: John Lister does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    He has 2 Chinese dependants
    He has received 23 Weekly Gross Income payments of £475.67 in the 182 day period from the same employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JL123456B  |
            | Application Raised Date | 2015-07-10 |
            | Dependant               | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mr                 |
            | Individual forename        | John               |
            | Individual surname         | Lister             |
            | Application Raised to date | 2015-01-09         |
            | Application Raised date    | 2015-07-10         |
            | National Insurance Number  | JL123456B          |
            | Threshold                  | 476.92             |

#New scenario - Added in
    Scenario: Gary Goldstein does not meet the Category A employment duration Requirement (He has worked for his current employer for only 20 weeks)

    He has 3 Isreali dependants
    He has received 20 Weekly Gross Income payments of £516.67 in the 182 day period from the same employer
    He worked for a different employer before his current employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | GG987654A  |
            | Application Raised Date | 2015-09-03 |
            | Dependant               | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mr                 |
            | Individual forename        | Gary               |
            | Individual surname         | Goldstein          |
            | Application Raised to date | 2015-03-05         |
            | Application Raised date    | 2015-09-03         |
            | National Insurance Number  | GG987654A          |
            | Threshold                  | 523.08             |
