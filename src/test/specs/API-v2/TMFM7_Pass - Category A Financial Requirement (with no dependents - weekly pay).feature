Feature: Pass - Category A Financial Requirement (with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of => £357.69 in the 182 day period prior to the Application Raised Date

#New Scenario -
    Scenario: Molly Henry meets the Category A Financial Requirement

    He has received 26 Weekly Gross Income payments of £470.43

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number| PAYE Reference | Employer         |
            | 2015-11-27 | 470.43 |    26       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-20 | 470.43 |    25       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-13 | 470.43 |    24       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-06 | 470.43 |    23       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-30 | 470.43 |    22       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-23 | 470.43 |    21       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-16 | 470.43 |    20       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-09 | 470.43 |    19       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-02 | 470.43 |    18       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-09-25 | 470.43 |    17       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-09-18 | 470.43 |    16       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-09-11 | 470.43 |    15       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-09-04 | 470.43 |    14       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-08-28 | 470.43 |    13       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-08-21 | 470.43 |    12       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-08-14 | 470.43 |    11       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-08-07 | 470.43 |    10       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-07-31 | 470.43 |    09       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-07-24 | 470.43 |    08       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-07-17 | 470.43 |    07       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-07-10 | 470.43 |    06       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-07-03 | 470.43 |    05       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-06-26 | 470.43 |    04       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-06-19 | 470.43 |    03       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-06-12 | 470.43 |    02       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-06-05 | 470.43 |    01       |            | FP/Ref1         | Flying Pizza Ltd |

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

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number| PAYE Reference | Employer         |
            | 2015-04-03 | 357.69 |    26       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-03-27 | 357.69 |    25       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-03-20 | 357.69 |    24       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-03-13 | 357.69 |    23       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-03-06 | 357.69 |    22       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-27 | 357.69 |    21       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-20 | 357.69 |    20       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-13 | 357.69 |    19       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-06 | 357.69 |    18       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-30 | 357.69 |    17       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-23 | 357.69 |    16       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-16 | 357.69 |    15       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-09 | 357.69 |    14       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-02 | 357.69 |    13       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-30 | 357.69 |    12       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-19 | 357.69 |    11       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-12 | 357.69 |    10       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-05 | 357.69 |    09       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-28 | 357.69 |    08       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-21 | 357.69 |    07       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-14 | 357.69 |    06       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-07 | 357.69 |    05       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-31 | 357.69 |    04       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-24 | 357.69 |    03       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-17 | 357.69 |    02       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-10-10 | 357.69 |    01       |            | FP/Ref1         | Flying Pizza Ltd |


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

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-06-26 | 1000.00 |    26       |              |                |                  |
            | 2015-06-19 | 1000.00 |    25       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 1000.00 |    24       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 1000.00 |    23       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 1000.00 |    22       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 1000.00 |    21       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 1000.00 |    20       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-08 | 1000.00 |    19       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-01 | 1000.00 |    18       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-04-24 | 1000.00 |    17       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-04-17 | 1000.00 |    16       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-04-10 | 1000.00 |    15       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-04-03 | 1000.00 |    14       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-03-27 | 1000.00 |    13       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-03-20 | 1000.00 |    12       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-03-13 | 1000.00 |    11       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-03-06 | 1000.00 |    10       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-27 | 1000.00 |    09       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-20 | 1000.00 |    08       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-13 | 1000.00 |    07       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-06 | 1000.00 |    06       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-30 | 1000.00 |    05       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-23 | 1000.00 |    04       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-16 | 1000.00 |    03       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-09 | 1000.00 |    02       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-02 | 1000.00 |    01       |              | FP/Ref1        | Flying Pizza Ltd |

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

