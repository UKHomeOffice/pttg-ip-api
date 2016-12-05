Feature: Pass - Category A Financial Requirement (with Dependants - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial income regulation to pass this Feature File
    Income required amount no dependant child = £18600 (£1550 per month or above for EACH of the previous 6 months from the Application Raised Date)
    Additional funds for 1 dependant child = £3800 on top of employment threshold
    Additional funds for EVERY subsequent dependant child = £2400 on top of employment threshold per child

    Financial income calculation to pass this Feature File
    Income required amount + 1 dependant amount + (Additional dependant amount * number of dependants)/52 weeks in the year = 26 Weekly Gross Income payments => threshold in the 182 day period prior to the Application Raised Date

    1 Dependant child - £18600+£3800/52 = £430.77
    2 Dependant children - £18600+£3800+£2400/12 = £476.92
    3 Dependant children - £18600+£3800+(£2400*2)/12 = £523.08
    5 Dependant children - £18600+£3800+(£2400*4)/12 = £615.38
    7 Dependant children - £18600+£3800+(£2400*6)/12 = £707.69
    ETC

#New scenario - Added
    Scenario: Tony Singh meets the Category A Financial Requirement with 1 Dependants

    He has received 26 Weekly Gross Income payments of £466.01
    He has 1 Dependants child

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | TS123456A  |
            | Application raised date | 2015-02-23 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mr               |
            | Individual forename       | Tony             |
            | Individual surname        | Singh            |
            | Assessment start date     | 2014-08-25       |
            | Application Raised date   | 2015-02-23       |
            | National Insurance Number | TS123456A        |
            | Threshold                 | 430.77           |
            | Employer Name             | Flying Pizza Ltd |


#New scenario - Added in SD126
    Scenario: Jennifer Toure meets the Category A Financial Requirement with 3 Dependants

    He has received 26 Weekly Gross Income payments of £606.00
    He has 3 Dependants child

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JT123456C  |
            | Application raised date | 2015-12-04 |
            | Dependants              | 3          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Miss             |
            | Individual forename       | Jennifer         |
            | Individual surname        | Toure            |
            | Assessment start date     | 2015-06-05       |
            | Application Raised date   | 2015-12-04       |
            | National Insurance Number | JT123456C        |
            | Threshold                 | 523.08           |
            | Employer Name             | Flying Pizza Ltd |


#New scenario - Added in
    Scenario: Lela Vasquez meets the Category A Financial Requirement with 5 Dependants

    He has received 26 Weekly Gross Income payments of £615.38
    He has 5 Dependants child

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | LV987654B  |
            | Application raised date | 2015-07-22 |
            | Dependants              | 5          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mrs              |
            | Individual forename       | Lela             |
            | Individual surname        | Vasquez          |
            | Assessment start date     | 2015-01-21       |
            | Application Raised date   | 2015-07-22       |
            | National Insurance Number | LV987654B        |
            | Threshold                 | 615.38           |
            | Employer Name             | Flying Pizza Ltd |

