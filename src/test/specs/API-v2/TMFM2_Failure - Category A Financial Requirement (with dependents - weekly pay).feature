Feature: Failure - Category A Financial Requirement  - with Dependants - weekly pay

    Requirement to meet Category A
    Applicant or Sponsor has received < 26 payments from the same employer over 6 month period prior to the Application Raised Date

    Financial income regulation to pass this Feature File
    Income required amount no dependant child = £18600 (£1550 per month or above for EACH of the previous 6 months from the Application Raised Date)
    Additional funds for 1 dependant child = £3800 on top of employment threshold
    Additional funds for EVERY subsequent dependant child = £2400 on top of employment threshold per child

    Financial income calculation to pass this Feature File
    Income required amount + 1 dependant amount + (Additional dependant amount * number of dependants)/52 weeks in the year = 26 Weekly Gross Income payments < threshold in the 6 month period prior to the Application Raised Date from the same employer

    1 Dependant child - £18600+£3800/52 = £430.77
    2 Dependant children - £18600+£3800+£2400/12 = £476.92
    3 Dependant children - £18600+£3800+(£2400*2)/12 = £523.08
    5 Dependant children - £18600+£3800+(£2400*4)/12 = £615.38
    7 Dependant children - £18600+£3800+(£2400*6)/12 = £707.69
    ETC

    Background: Thresholds are configured to default values
        Given The yearly threshold is configured to 18600:
        And The single dependant yearly threshold is configured to 22400:
        And The remaining dependant increment is configured to 2400:

#New scenario - Added in
    Scenario: Donald Sweet does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)
    Pay date - Weekly (variable dates)
    Before day of Application Raised Date
    He has 3 columbian dependants
    He has received 26 Weekly Gross Income payments of £225.40 in the 6 month period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 225.40 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 225.40 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 225.40 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 225.40 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 225.40 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 225.40 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 225.40 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 225.40 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 225.40 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 225.40 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 225.40 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 225.40 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 225.40 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 225.40 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 225.40 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 225.40 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 225.40 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 225.40 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 225.40 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 225.40 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 225.40 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 225.40 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 225.40 | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 225.40 | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 225.40 | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-08 | 225.40 | 01          |              | FP/Ref1        | Flying Pizza Ltd |



        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - applicant        | AP123456C  |
            | Application Raised Date | 2015-11-03 |
            | dependants              | 3          |


        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                          |
            | Applicant                | National Insurance Number | AP123456C                    |
            | Category A Weekly Salary | Financial requirement met | false                        |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03                   |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03                   |
            | Category A Weekly Salary | Threshold                 | 523.08                       |
            | Category A Weekly Salary | Employer Name - AP123456C | Flying Pizza Ltd             |


#New scenario - Added in SD126
    Scenario: John Lister does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold also does not have enough records)

    He has 2 Chinese dependants
    He has received 23 Weekly Gross Income payments of £475.67 in the 6 month period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 475.67 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 475.67 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 475.67 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 475.67 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 475.67 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 475.67 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 475.67 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 475.67 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 475.67 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 475.67 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 475.67 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 475.67 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 475.67 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 475.67 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 475.67 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 475.67 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 475.67 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 475.67 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 475.67 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 475.67 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 475.67 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 475.67 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 475.67 | 04          |              | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JL123456D  |
            | Application Raised Date | 2015-11-03 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                |
            | Applicant                | National Insurance Number | JL123456D          |
            | Category A Weekly Salary | Financial requirement met | false              |
            | Category A Weekly Salary | Failure reason            | NOT_ENOUGH_RECORDS |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03         |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03         |
            | Category A Weekly Salary | Threshold                 | 476.92             |
            | Category A Weekly Salary | Employer Name - JL123456D | Flying Pizza Ltd   |

#New scenario - Added in
    Scenario: Gary Goldstein does not meet the Category A employment duration Requirement (He has worked for his current employer for only 20 weeks)

    He has 3 Isreali dependants
    He has received 20 Weekly Gross Income payments of £530.67 and 6 Weekly Gross payments of £525.30 in the 6 month period from two employers
    He worked for a different employer before his current employer

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-08-27 | 530.67 | 26          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-20 | 530.67 | 25          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-14 | 530.67 | 24          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-07 | 530.67 | 23          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-31 | 530.67 | 22          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-24 | 530.67 | 21          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-17 | 530.67 | 20          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-10 | 530.67 | 19          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-03 | 530.67 | 18          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-26 | 530.67 | 17          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-19 | 530.67 | 16          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-12 | 530.67 | 15          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-05 | 530.67 | 14          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-29 | 530.67 | 13          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-22 | 530.67 | 12          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-15 | 530.67 | 11          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-08 | 530.67 | 10          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-01 | 530.67 | 09          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-24 | 530.67 | 08          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-17 | 530.67 | 07          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-10 | 525.30 | 06          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-04-03 | 525.30 | 05          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-27 | 525.30 | 04          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-20 | 525.30 | 03          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-13 | 525.30 | 02          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-06 | 525.30 | 01          |              | FP/Ref1        | Curry House Ltd  |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | GG987654A  |
            | Application Raised Date | 2015-09-03 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                               |
            | Applicant                | National Insurance Number | GG987654A                         |
            | Category A Weekly Salary | Financial requirement met | false                             |
            | Category A Weekly Salary | Failure reason            | MULTIPLE_EMPLOYERS                |
            | Category A Weekly Salary | Assessment start date     | 2015-03-03                        |
            | Category A Weekly Salary | Application Raised date   | 2015-09-03                        |
            | Category A Weekly Salary | Threshold                 | 523.08                            |
            | Category A Weekly Salary | Employer Name - GG987654A | Flying Pizza Ltd, Curry House Ltd |

#New scenario - Added on 24th July 2017
    Scenario: Terry Pilchard does not meet the Category A Employment Requirement (He currently works for two employers)

    He has 3 Isreali dependants
    He has received 26 Weekly Gross Income payments of £260.60 and £300.00 in the 6 month period from two active employers.
    Combined the totals are above the threshold, however this will trigger a failed result.

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-08-27 | 300.00 | 26          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-27 | 260.60 | 26          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-08-20 | 300.00 | 25          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-20 | 260.60 | 25          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-08-14 | 300.00 | 24          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-14 | 260.60 | 24          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-08-07 | 300.00 | 23          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-08-07 | 260.60 | 23          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-07-31 | 300.00 | 22          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-31 | 260.60 | 22          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-07-24 | 300.00 | 21          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-24 | 260.60 | 21          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-07-17 | 300.00 | 20          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-17 | 260.60 | 20          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-07-10 | 300.00 | 19          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-10 | 260.60 | 19          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-07-03 | 300.00 | 18          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-07-03 | 260.60 | 18          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-06-26 | 300.00 | 17          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-26 | 260.60 | 17          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-06-19 | 300.00 | 16          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-19 | 260.60 | 16          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-06-12 | 300.00 | 15          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-12 | 260.60 | 15          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-06-05 | 300.00 | 14          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-06-05 | 260.60 | 14          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-05-29 | 300.00 | 13          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-29 | 260.60 | 13          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-05-22 | 300.00 | 12          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-22 | 260.60 | 12          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-05-15 | 300.00 | 11          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-15 | 260.60 | 11          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-05-08 | 300.00 | 10          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-08 | 260.60 | 10          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-05-01 | 300.00 | 09          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-05-01 | 260.60 | 09          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-04-24 | 300.00 | 08          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-24 | 260.60 | 08          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-04-17 | 300.00 | 07          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-17 | 260.60 | 07          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-04-10 | 300.00 | 06          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-10 | 260.60 | 06          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-04-03 | 300.00 | 05          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-04-03 | 260.60 | 05          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-27 | 300.00 | 04          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-03-27 | 260.60 | 04          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-20 | 300.00 | 03          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-03-20 | 260.60 | 03          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-13 | 300.00 | 02          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-03-13 | 260.60 | 02          |              | FP/Ref1        | Curry House Ltd  |
            | 2015-03-06 | 300.00 | 01          |              | FP/Ref2        | Flying Pizza Ltd |
            | 2015-03-06 | 260.60 | 01          |              | FP/Ref1        | Curry House Ltd  |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | GG987654A  |
            | Application Raised Date | 2015-09-03 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                               |
            | Applicant                | National Insurance Number | GG987654A                         |
            | Category A Weekly Salary | Financial requirement met | false                             |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD      |
            | Category A Weekly Salary | Assessment start date     | 2015-03-03                        |
            | Category A Weekly Salary | Application Raised date   | 2015-09-03                        |
            | Category A Weekly Salary | Threshold                 | 523.08                            |
            | Category A Weekly Salary | Employer Name - GG987654A | Curry House Ltd, Flying Pizza Ltd |

#New scenario - Added on 24th July 2017
    Scenario: Benedict Smythe does not meet the Category A Employment Payment Frequency Requirement
    (He passes the Cat A financial threshold & he has worked for the same employer but his payment frequency has changed)

    He has 3 Canadian dependants
    He was received 18 Weekly Gross income payments of £525.00 and then 2 Monthly Gross income payments of £2266.68 in the 182 period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 2266.68 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-30 | 2266.68 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 525.00  | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 525.00  | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 525.00  | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 525.00  | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 525.00  | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 525.00  | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 525.00  | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 525.00  | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 525.00  | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 525.00  | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 525.00  | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 525.00  | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 525.00  | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 525.00  | 01          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JW984624A  |
            | Application Raised Date | 2015-11-22 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                           | HTTP Status               | 200                  |
            | Applicant                               | National Insurance Number | JW984624A            |
            | Category A Unsupported Salary Frequency | Financial requirement met | false                |
            | Category A Unsupported Salary Frequency | Failure reason            | PAY_FREQUENCY_CHANGE |
            | Category A Unsupported Salary Frequency | Assessment start date     | 2015-05-22           |
            | Category A Unsupported Salary Frequency | Application Raised date   | 2015-11-22           |
            | Category A Unsupported Salary Frequency | Threshold                 | 0                    |
            | Category A Unsupported Salary Frequency | Employer Name - JW984624A | Flying Pizza Ltd     |


####### New scenario - 31st July

    Scenario: Jill has 1 dependent and does not meet the Category A Financial Requirement.
    No records could be found for Jill


        Given HMRC has the following income records:
            | Date | Amount | Week Number | Month Number | PAYE Reference | Employer |



        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JL123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response             | HTTP Status               | 200                |
            | Applicant                 | National Insurance Number | JL123456A          |
            | Category A Monthly Salary | Financial requirement met | false              |
            | Category A Monthly Salary | Failure reason            | NOT_ENOUGH_RECORDS |
            | Category A Monthly Salary | Assessment start date     | 2014-07-15         |
            | Category A Monthly Salary | Application Raised date   | 2015-01-15         |
