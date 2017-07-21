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

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-2-23 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-15 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-8 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-1 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-25 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-18 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-11 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-4 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-28 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-21 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-14 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-7 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-30 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-23 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-20 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-14 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-7 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-31 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-24 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-17 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-10 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-3 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-26 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-19 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-12 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-25 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-23 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-21 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-16 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-9 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-2 |     466.01 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | TS123456A  |
            | Application raised date | 2015-02-23 |
            | Dependants              | 1          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2014-08-25       |
            | Application Raised date   | 2015-02-23       |
            | National Insurance Number | TS123456A        |
            | Threshold                 | 430.77           |
            | Employer Name             | Flying Pizza Ltd |


#New scenario - Added in SD126
    Scenario: Jennifer Toure meets the Category A Financial Requirement with 3 Dependants

    He has received 26 Weekly Gross Income payments of £606.00
    He has 3 Dependants child

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-12-10 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-12-03 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-27 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-20 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-13 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-6 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-29 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-22 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-15 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-8 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-1 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-24 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-17 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-10 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-3 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-26 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-19 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-12 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-5 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-29 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-22 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-15 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-8 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-1 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-25 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-12 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-5 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-04 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-27 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-20 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-13 |     606.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JT123456C  |
            | Application raised date | 2015-12-04 |
            | Dependants              | 3          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2015-06-05       |
            | Application Raised date   | 2015-12-04       |
            | National Insurance Number | JT123456C        |
            | Threshold                 | 523.08           |
            | Employer Name             | Flying Pizza Ltd |


#New scenario - Added in
    Scenario: Lela Vasquez meets the Category A Financial Requirement with 5 Dependants

    He has received 26 Weekly Gross Income payments of £615.38
    He has 5 Dependants child

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-07-22 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-07-15 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-07-08 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-07-01 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-24 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-17 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-10 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-3 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-26 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-19 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-12 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-5 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-28 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-21 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-14 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-7 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-31 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-24 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-17 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-10 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-3 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-26 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-19 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-12 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-5 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-21 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-21 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-15 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-8 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-1 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-24 |     615.38 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | LV987654B  |
            | Application raised date | 2015-07-22 |
            | Dependants              | 5          |
        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Financial requirement met | true             |
            | Assessment start date     | 2015-01-21       |
            | Application Raised date   | 2015-07-22       |
            | National Insurance Number | LV987654B        |
            | Threshold                 | 615.38           |
            | Employer Name             | Flying Pizza Ltd |

