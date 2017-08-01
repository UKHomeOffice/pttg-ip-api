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
            | Date       | Amount | Week Number | Month Number| PAYE Reference| Employer        |
            | 2016-02-20 | 466.01 |    26       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-02-13 | 466.01 |    25       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-02-06 | 466.01 |    24       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-01-30 | 466.01 |    23       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-01-23 | 466.01 |    22       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-01-16 | 466.01 |    21       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-01-09 | 466.01 |    20       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2016-01-02 | 466.01 |    19       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-30 | 466.01 |    18       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-19 | 466.01 |    17       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-12 | 466.01 |    16       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-05 | 466.01 |    15       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-28 | 466.01 |    14       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-21 | 466.01 |    13       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-14 | 466.01 |    12       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-07 | 466.01 |    11       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-31 | 466.01 |    10       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-24 | 466.01 |    09       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-17 | 466.01 |    08       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-10 | 466.01 |    07       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-03 | 466.01 |    06       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-26 | 466.01 |    05       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-19 | 466.01 |    04       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-12 | 466.01 |    03       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-05 | 466.01 |    02       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-29 | 466.01 |    01       |            | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving TM Family API is invoked with the following:
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
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-11-27 | 606.00 |    26       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-11-20 | 606.00 |    25       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-11-13 | 606.00 |    24       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-11-06 | 606.00 |    23       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-30 | 606.00 |    22       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 606.00 |    21       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 606.00 |    20       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 606.00 |    19       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 606.00 |    18       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 606.00 |    17       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 606.00 |    16       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 606.00 |    15       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 606.00 |    14       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 606.00 |    13       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 606.00 |    12       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 606.00 |    11       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 606.00 |    10       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 606.00 |    09       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 606.00 |    08       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 606.00 |    07       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 606.00 |    06       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 606.00 |    05       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 606.00 |    04       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 606.00 |    03       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 606.00 |    02       |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 606.00 |    01       |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving TM Family API is invoked with the following:
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
        | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
        | 2015-07-17 | 615.38 |    26       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-07-10 | 615.38 |    25       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-07-03 | 615.38 |    24       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-06-26 | 615.38 |    23       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-06-19 | 615.38 |    22       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-06-12 | 615.38 |    21       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-06-05 | 615.38 |    20       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-05-29 | 615.38 |    19       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-05-22 | 615.38 |    18       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-05-15 | 615.38 |    17       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-05-08 | 615.38 |    16       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-05-01 | 615.38 |    15       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-04-24 | 615.38 |    14       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-04-17 | 615.38 |    13       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-04-10 | 615.38 |    12       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-04-03 | 615.38 |    11       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-03-27 | 615.38 |    10       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-03-20 | 615.38 |    09       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-03-13 | 615.38 |    08       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-03-06 | 615.38 |    07       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-02-27 | 615.38 |    06       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-02-20 | 615.38 |    05       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-02-13 | 615.38 |    04       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-02-06 | 615.38 |    03       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-01-30 | 615.38 |    02       |              | FP/Ref1        | Flying Pizza Ltd |
        | 2015-01-23 | 615.38 |    01       |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving TM Family API is invoked with the following:
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

