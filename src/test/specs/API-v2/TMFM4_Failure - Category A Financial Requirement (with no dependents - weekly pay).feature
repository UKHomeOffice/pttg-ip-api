Feature: Failure - Category A Financial Requirement with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received < 26 payments from the same employer over 6 month period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of < £357.69 in the 6 month period prior to the Application Raised Date

    Background: Thresholds are configured to default values
        Given The yearly threshold is configured to 18600:
        And The single dependant yearly threshold is configured to 22400:
        And The remaining dependant increment is configured to 2400:

#New Scenario -
    Scenario: Davina Love does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    She earns £300.11 weekly Gross Income EVERY of the 26 weeks

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 300.11 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 300.11 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 300.11 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 300.11 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 300.11 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 300.11 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 300.11 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 300.11 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 300.11 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 300.11 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 300.11 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 300.11 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 300.11 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 300.11 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 300.11 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 300.11 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 300.11 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 300.11 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 300.11 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 300.11 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 300.11 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 300.11 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 300.11 | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 300.11 | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 300.11 | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-08 | 300.11 | 01          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SP123456A  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                          |
            | Applicant                | National Insurance Number | SP123456A                    |
            | Category A Weekly Salary | Financial requirement met | false                        |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03                   |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03                   |
            | Category A Weekly Salary | Threshold                 | 357.69                       |
            | Category A Weekly Salary | Employer Name - SP123456A | Flying Pizza Ltd             |




#New Scenario -
    Scenario: Paul Young does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    He earns £400.99 weekly Gross Income EVERY of the 24 weeks
    and he earns £300.99 weekly Gross Income for the LAST 2 weeks (total 26 weeks with the same employer)

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 300.99 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 300.99 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 400.99 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 400.99 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 400.99 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 400.99 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 400.99 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 400.99 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 400.99 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 400.99 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 400.99 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 400.99 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 400.99 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 400.99 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 400.99 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 400.99 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 400.99 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 400.99 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 400.99 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 400.99 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 400.99 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 400.99 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 400.99 | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 400.99 | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 400.99 | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-08 | 400.99 | 01          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PY123456B  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                          |
            | Applicant                | National Insurance Number | PY123456B                    |
            | Category A Weekly Salary | Financial requirement met | false                        |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03                   |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03                   |
            | Category A Weekly Salary | Threshold                 | 357.69                       |
            | Category A Weekly Salary | Employer Name - PY123456B | Flying Pizza Ltd             |

#New Scenario -
    Scenario: Raj Patel does not meet the Category A employment duration Requirement (He has worked for his current employer for only 3 months)

    He earns £600 a Week Gross Income BUT for only 12 weeks
    He worked for a different employer before his current employer


        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 600.00 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 600.00 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 600.00 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 600.00 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 600.00 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 600.00 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 600.00 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 600.00 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 600.00 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 600.00 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 600.00 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 600.00 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 400.00 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 400.99 | 13          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-07-24 | 400.99 | 12          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-07-17 | 400.99 | 11          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-07-10 | 400.99 | 10          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-07-03 | 400.99 | 09          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-06-26 | 400.99 | 08          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-06-19 | 400.99 | 07          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-06-12 | 400.99 | 06          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-06-05 | 400.99 | 05          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-05-29 | 400.99 | 04          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-05-22 | 400.99 | 03          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-05-15 | 400.99 | 02          |              | FP/Ref2        | Crazy Pizza  Ltd |
            | 2015-05-08 | 400.99 | 01          |              | FP/Ref2        | Crazy Pizza  Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | RP123456C  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                                |
            | Applicant                | National Insurance Number | RP123456C                          |
            | Category A Weekly Salary | Financial requirement met | false                              |
            | Category A Weekly Salary | Failure reason            | MULTIPLE_EMPLOYERS                 |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03                         |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03                         |
            | Category A Weekly Salary | Threshold                 | 357.69                             |
            | Category A Weekly Salary | Employer Name - RP123456C | Flying Pizza Ltd, Crazy Pizza  Ltd |


#New Scenario -
    Scenario: John James does not meet the Category A employment duration Requirement (He has worked for his current employer for slightly less than 6 months)

    He earns £357.70 a Week Gross Income BUT for 25 weeks


        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 357.70 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 357.70 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 357.70 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 357.70 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 357.70 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 357.70 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 357.70 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 357.70 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 357.70 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 357.70 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 357.70 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 357.70 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 357.70 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 357.70 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 357.70 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 357.70 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 357.70 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 357.70 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 357.70 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 357.70 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 357.70 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 357.70 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 357.70 | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 357.70 | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 357.70 | 02          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JJ123456A  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                |
            | Applicant                | National Insurance Number | JJ123456A          |
            | Category A Weekly Salary | Financial requirement met | false              |
            | Category A Weekly Salary | Failure reason            | NOT_ENOUGH_RECORDS |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03         |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03         |
            | Category A Weekly Salary | Threshold                 | 357.69             |
            | Category A Weekly Salary | Employer Name - JJ123456A | Flying Pizza Ltd   |


    Scenario: Gary Goldstein does not meet the Category A Employment Requirement (He currently works for two employers)

    He has received 26 Weekly Gross Income payments of £260.60 and £300.00 in the 6 month period from two active employers.  Combined the totals are above the threshold, however this will trigger a failed result.

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

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                               |
            | Applicant                | National Insurance Number | GG987654A                         |
            | Category A Weekly Salary | Financial requirement met | false                             |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD      |
            | Category A Weekly Salary | Assessment start date     | 2015-03-03                        |
            | Category A Weekly Salary | Application Raised date   | 2015-09-03                        |
            | Category A Weekly Salary | Threshold                 | 357.69                            |
            | Category A Weekly Salary | Employer Name - GG987654A | Curry House Ltd, Flying Pizza Ltd |



#New Scenario -

    Scenario: Peter Jones does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    He earns £658.50 a Week Gross Income BUT for 25 weeks
    He worked for a different employer before his current employer on week 1 and earned £357

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 658.50 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 658.50 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 658.50 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 658.50 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 658.50 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 658.50 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 658.50 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 658.50 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 658.50 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 658.50 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 658.50 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 658.50 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 658.50 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 658.50 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 658.50 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 658.50 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 658.50 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 658.50 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 658.50 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 658.50 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 658.50 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 658.50 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 658.50 | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-22 | 658.50 | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-15 | 658.50 | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-08 | 357.00 | 01          |              | MP/Ref1        | Mambo Pizza Ltd  |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PJ123456A  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                               |
            | Applicant                | National Insurance Number | PJ123456A                         |
            | Category A Weekly Salary | Financial requirement met | false                             |
            | Category A Weekly Salary | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD      |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03                        |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03                        |
            | Category A Weekly Salary | Threshold                 | 357.69                            |
            | Category A Weekly Salary | Employer Name - PJ123456A | Flying Pizza Ltd, Mambo Pizza Ltd |

#New Scenario -
    Scenario: Jo Francis does not meet the Category A employment duration Requirement (He has worked for his current employer for less than 6 months)

    She earns £658.50 a Week Gross Income BUT for 23 weeks

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-10-30 | 658.50 | 26          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-23 | 658.50 | 25          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-16 | 658.50 | 24          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-09 | 658.50 | 23          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-10-02 | 658.50 | 22          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-25 | 658.50 | 21          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-18 | 658.50 | 20          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-11 | 658.50 | 19          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-09-04 | 658.50 | 18          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-28 | 658.50 | 17          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-21 | 658.50 | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-14 | 658.50 | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-08-07 | 658.50 | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-31 | 658.50 | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-24 | 658.50 | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-17 | 658.50 | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-10 | 658.50 | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-07-03 | 658.50 | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-26 | 658.50 | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-19 | 658.50 | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-12 | 658.50 | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-06-05 | 658.50 | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-05-29 | 658.50 | 04          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PP123456A  |
            | Application Raised Date | 2015-11-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response            | HTTP Status               | 200                |
            | Applicant                | National Insurance Number | PP123456A          |
            | Category A Weekly Salary | Financial requirement met | false              |
            | Category A Weekly Salary | Failure reason            | NOT_ENOUGH_RECORDS |
            | Category A Weekly Salary | Assessment start date     | 2015-05-03         |
            | Category A Weekly Salary | Application Raised date   | 2015-11-03         |
            | Category A Weekly Salary | Threshold                 | 357.69             |
            | Category A Weekly Salary | Employer Name - PP123456A | Flying Pizza Ltd   |


    Scenario: Jenny Francis does not meet the Category A Employment Payment Frequency Requirement
    (She passes the Cat A financial threshold & She has worked for the same employer but her payment frequency has changed)

    She has 3 Canadian dependants
    She was received 18 Weekly Gross income payments of £525.00 and then 2 Monthly Gross income payments of £2266.68 in the 182 period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2015-04-24 | 2266.68 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-03-27 | 2266.68 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-27 | 525.00  | 16          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-20 | 525.00  | 15          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-13 | 525.00  | 14          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-02-06 | 525.00  | 13          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-30 | 525.00  | 12          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-23 | 525.00  | 11          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-16 | 525.00  | 10          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-09 | 525.00  | 09          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-01-02 | 525.00  | 08          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-12-26 | 525.00  | 07          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-12-19 | 525.00  | 06          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-12-12 | 525.00  | 05          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-12-05 | 525.00  | 04          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2015-11-28 | 525.00  | 03          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-21 | 525.00  | 02          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2014-11-14 | 525.00  | 01          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PP123456A  |
            | Application Raised Date | 2015-05-12 |


        Then The Income Proving TM Family API provides the following result:
            | HTTP Response                           | HTTP Status               | 200                  |
            | Applicant                               | National Insurance Number | PP123456A            |
            | Category A Unsupported Salary Frequency | Financial requirement met | false                |
            | Category A Unsupported Salary Frequency | Failure reason            | PAY_FREQUENCY_CHANGE |
            | Category A Unsupported Salary Frequency | Assessment start date     | 2014-11-12           |
            | Category A Unsupported Salary Frequency | Application Raised date   | 2015-05-12           |
            | Category A Unsupported Salary Frequency | Threshold                 | 0                    |
            | Category A Unsupported Salary Frequency | Employer Name - PP123456A | Flying Pizza Ltd     |
