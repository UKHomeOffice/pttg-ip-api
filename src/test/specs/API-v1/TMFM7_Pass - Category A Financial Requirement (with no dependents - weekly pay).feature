Feature: Pass - Category A Financial Requirement (with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of => £357.69 in the 182 day period prior to the Application Raised Date

#New Scenario -
    Scenario: Molly Henry meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £470.43

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | MH123456A  |
            | Application raised date | 2015-11-29 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Ms               |
            | Individual forename       | Molly            |
            | Individual surname        | Henry            |
            | Assessment start date     | 2015-05-31       |
            | Application Raised date   | 2015-11-29       |
            | National Insurance Number | MH123456A        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Fernando Sanchez meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £357.69

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | FS123456C  |
            | Application raised date | 2015-04-10 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mr               |
            | Individual forename       | Fernando         |
            | Individual surname        | Sanchez          |
            | Assessment start date     | 2014-10-10       |
            | Application Raised date   | 2015-04-10       |
            | National Insurance Number | FS123456C        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Jonathan Odometey meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £1000.00

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JO123456A  |
            | Application raised date | 2015-06-28 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Individual title          | Mr               |
            | Individual forename       | John             |
            | Individual surname        | Odometey         |
            | Assessment start date     | 2014-12-28       |
            | Application Raised date   | 2015-06-28       |
            | National Insurance Number | JO123456A        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |

