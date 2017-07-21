Feature: Pass - Category A Financial Requirement (with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of => £357.69 in the 182 day period prior to the Application Raised Date

#New Scenario -
    Scenario: Molly Henry meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £470.43

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-11-29 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-20 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-13 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-06 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-30 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-23 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-16 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-9 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-2 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-25 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-18 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-11 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-04 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-28 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-21 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-14 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-07 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-31 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-24 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-17 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-10 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-07-03 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-26 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-19 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-12 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-31 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-27 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-22 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-15 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-08 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-01 |     470.43 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | MH123456A  |
            | Application raised date | 2015-11-29 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2015-05-31       |
            | Application Raised date   | 2015-11-29       |
            | National Insurance Number | MH123456A        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Fernando Sanchez meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £357.69

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-10-9 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-8 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-7 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-31 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-24 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-17 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-10 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-3 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-27 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-20 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-13 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-6 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-27 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-20 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-13 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-6 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-30 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-23 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-16 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-9 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-2 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-25 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-18 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-11 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-4 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-10 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-21 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-14 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-7 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-28 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-21 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-14 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-7 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-28 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-21 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-14 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-7 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-31 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-24 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-17 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-10 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-03 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-27 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-20 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-13 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-6 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-29 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-22 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-15 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-08 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-01 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-25 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-18 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-11 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-04 |     357.69 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FS123456C  |
            | Application raised date | 2015-04-10 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-10-10       |
            | Application Raised date   | 2015-04-10       |
            | National Insurance Number | FS123456C        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |


#New Scenario -
    Scenario: Jonathan Odometey meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £1000.00

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-7-21 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-14 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-7 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-30 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-23 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-16 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-9 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-2 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-26 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-19 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-12 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-5 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-28 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-21 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-14 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-7 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-31 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-24 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-17 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-10 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-3 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-24 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-17 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-10 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-3 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-27 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-20 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-13 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-6 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-29 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-22 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JO123456A  |
            | Application raised date | 2015-06-28 |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-12-28       |
            | Application Raised date   | 2015-06-28       |
            | National Insurance Number | JO123456A        |
            | Threshold                 | 357.69           |
            | Employer Name             | Flying Pizza Ltd |

