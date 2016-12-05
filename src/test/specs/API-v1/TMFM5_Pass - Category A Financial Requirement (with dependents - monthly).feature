Feature: Pass - Category A Financial Requirement (with dependants - monthly)

    Requirement to meet Category A
    Applicant or Sponsor has received 6 consecutive monthly payments from the same employer over the 182 day period prior to the Application Raised Date

    Financial income regulation to pass this Feature File
    Income required amount no dependant child = £18600 (They have earned 6 monthly payments => £1550 Monthly Gross Income in the 182 days prior to the Application Raised Date)
    Additional funds for 1 dependant child = £3800 on top of employment threshold
    Additional funds for EVERY subsequent dependant child = £2400 on top of employment threshold per child

    Financial income calculation to pass this Feature File
    Income required amount + 1 dependant amount + (Additional dependant amount * number of dependants)/12 = Equal to or greater than the threshold Monthly Gross Income in the 182 days prior to the Application Raised Date

    1 Dependant child - £18600+£3800/12 = £1866.67
    2 Dependant children - £18600+£3800+£2400/12 = £2066.67
    3 Dependant children - £18600+£3800+(£2400*2)/12 = £2266.67
    5 Dependant children - £18600+£3800+(£2400*4)/12 = £2666.67
    7 Dependant children - £18600+£3800+(£2400*6)/12 = £3066.67
    ETC

#New scenario -
    Scenario: Tony Ledo meets the Category A Financial Requirement with 1 dependant

    Pay date 15th of the month
    Before day of application received date
    He earns £4166.67 Monthly Gross Income EVERY of the 6 months
    He has 1 dependant child

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | TL123456A  |
            | Application raised date | 2015-01-23 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mr               |
            | Individual forename       | Tony             |
            | Individual surname        | Ledo             |
            | Assessment start date     | 2014-07-25       |
            | Application Raised date   | 2015-01-23       |
            | National Insurance Number | TL123456A        |
            | Threshold                 | 1866.67          |
            | Employer Name             | Flying Pizza Ltd |


#New scenario -
    Scenario: Scarlett Jones meets the Category A Financial Requirement with 3 dependant

    Pay date 2nd of the month
    Before day of Application Raised Date
    He earns £3333.33 Monthly Gross Income EVERY of the 6 months
    He has 3 dependant child

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | SJ123456C  |
            | Application raised date | 2015-12-08 |
            | Dependants              | 3          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Ms               |
            | Individual forename       | Scarlett         |
            | Individual surname        | Jones            |
            | Assessment start date     | 2015-06-09       |
            | Application Raised date   | 2015-12-08       |
            | National Insurance Number | SJ123456C        |
            | Threshold                 | 2266.67          |
            | Employer Name             | Flying Pizza Ltd |

#New scenario -
    Scenario: Wasim Mohammed meets the Category A Financial Requirement with 5 dependants

    Pay date 30th of the month
    On the same day of Application Raised Date
    He earns £5833.33 Monthly Gross Income EVERY of the 6 months
    He has 5 dependant child23/07/

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | WA987654B  |
            | Application raised date | 2015-02-28 |
            | Dependants              | 5          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mr               |
            | Individual forename       | Wasim            |
            | Individual surname        | Mohammed         |
            | Assessment start date     | 2014-08-30       |
            | Application Raised date   | 2015-02-28       |
            | National Insurance Number | WA987654B        |
            | Threshold                 | 2666.67          |
            | Employer Name             | Flying Pizza Ltd |
