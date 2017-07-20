Feature: Failure - Category A Financial Requirement (with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received < 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of < £357.69 in the 182 day period prior to the Application Raised Date

#New Scenario -
    Scenario: Davina Love does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    She earns £300.11 weekly Gross Income EVERY of the 26 weeks

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-1-15 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-08 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-01 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-25 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-18 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-11 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-04 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-27 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-20 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-13 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-06 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-31 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-24 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-17 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-10 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-03 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-29 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-22 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-15 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-08 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-01 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-25 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-18 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-11 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-04 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-7-27 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-7-20 |     300.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | DV123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                          |
            | Financial requirement met | false                        |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-07-17                   |
            | Application Raised date   | 2015-01-15                   |
            | National Insurance Number | DV123456A                    |
            | Threshold                 | 357.69                       |
            | Employer Name             | Flying Pizza Ltd             |

#New Scenario -
    Scenario: Xavier Snow does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    He earns £30.99 weekly Gross Income EVERY of the 26 weeks

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-12-22 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-12-15 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-12-08 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-12-01 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-24 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-17 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-10 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-11-3 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-26 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-19 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-12 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-10-5 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-28 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-21 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-14 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-9-7 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-31 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-24 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-17 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-10 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-8-3 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-26 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-19 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-12 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-7-5 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-29 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-22 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-15 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-8 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-6-1 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-24 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-22 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-15 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-08 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-5-01 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-24 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-17 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-10 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-4-3 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-26 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-19 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-12 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-5 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-28 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-21 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-14 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-7 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-31 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-24 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-17 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-10 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-3 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-26 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-19 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-12 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-12 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-7 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-30 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-23 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-16 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-9 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-2 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-25 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-18 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-11 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-4 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-27 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-20 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-13 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-9-6 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-29 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-22 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-15 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-8 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-8-1 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-7-25 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-7-18 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-7-10 |      30.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | XS123456B  |
            | Application Raised Date | 2015-12-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                          |
            | Financial requirement met | false                        |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2015-06-16                   |
            | Application Raised date   | 2015-12-15                   |
            | National Insurance Number | XS123456B                    |
            | Threshold                 | 357.69                       |
            | Employer Name             | Flying Pizza Ltd             |


#New Scenario -
    Scenario: Paul Young does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    He earns £400.99 weekly Gross Income EVERY of the 24 weeks
    and he earns £300.99 weekly Gross Income for the LAST 2 weeks (total 26 weeks with the same employer)

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-01-15 |     300.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-24 |     300.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-18 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-11 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-12-04 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-27 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-20 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-13 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-11-06 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-30 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-23 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-16 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-09 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-10-02 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-25 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-18 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-11 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-09-04 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-28 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-21 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-14 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-08-07 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-07-31 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-07-24 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-07-20 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2014-07-17 |     400.99 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PY123456B  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                          |
            | Financial requirement met | false                        |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-07-17                   |
            | Application Raised date   | 2015-01-15                   |
            | National Insurance Number | PY123456B                    |
            | Threshold                 | 357.69                       |
            | Employer Name             | Flying Pizza Ltd             |

#New Scenario -
    Scenario: Raj Patel does not meet the Category A employment duration Requirement (He has worked for his current employer for only 3 months)

    He earns £600 a Week Gross Income BUT for only 12 weeks
    He worked for a different employer before his current employer

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-07-03 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-26 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-19 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-12 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-05 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-29 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-22 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-15 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-08 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-01 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-24 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-17 |     600.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-10 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-04-3 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-03-26 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-03-19 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-03-12 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-03-5 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-03-26 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-02-19 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-02-12 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-02-5 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-01-29 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-01-22 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-01-15 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-01-8 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
            | 2015-01-1 |     600.00 | 1           |            | 123/PD45678       | Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | RP123456C  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                        |
            | Financial requirement met | false                      |
            | Failure reason            | NON_CONSECUTIVE_MONTHS         |
            | Assessment start date     | 2015-01-02                 |
            | Application Raised date   | 2015-07-03                 |
            | National Insurance Number | RP123456C                  |
            | Threshold                 | 357.69                     |
            | Employer Name             | Flying Pizza Ltd,Pizza Ltd |


#New Scenario -
    Scenario: John James does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    He earns £357.70 a Week Gross Income BUT for 25 weeks
    He worked for a different employer before his current employer on week 26 and earned £357

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-07-03 |     357.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-26 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-19 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-12 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-05 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-29 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-22 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-15 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-08 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-01 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-24 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-17 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-10 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-03 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-27 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-20 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-13 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-06 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-27 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-20 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-13 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-06 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-30 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-23 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-16 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-09 |     357.70 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JJ123456A  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2015-01-02         |
            | Application Raised date   | 2015-07-03         |
            | National Insurance Number | JJ123456A          |
            | Threshold                 | 357.69             |
            | Employer Name             | Flying Pizza Ltd   |

#New Scenario -
    Scenario: Peter Jones does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    He earns £658.50 a Week Gross Income BUT for 25 weeks
    He worked for a different employer before his current employer on week 26 and earned £357

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-07-03 |     357.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-26 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-19 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-12 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-05 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-29 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-22 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-15 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-08 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-01 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-24 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-17 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-10 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-03 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-27 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-20 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-13 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-06 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-27 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-20 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-13 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-02-06 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-30 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-23 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-16 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-01-09 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PJ123456A  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2015-01-02         |
            | Application Raised date   | 2015-07-03         |
            | National Insurance Number | PJ123456A          |
            | Threshold                 | 357.69             |
            | Employer Name             | Flying Pizza Ltd   |

#New Scenario -
    Scenario: Jenny Francis does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    She earns £658.50 a Week Gross Income BUT for 23 weeks

        Given HMRC has the following income records:
            | Date       | Amount     | Week Number | Month Number| PAYE Reference   | Employer         |
            | 2015-05-12 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-06-5 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-28 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-21 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-14 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-05-7 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-30 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-24 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-17 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-10 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-04-03 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-03-24 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-17 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-10 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-3-03 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-27 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-20 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-13 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-2-6 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-30 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-23 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-16 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
            | 2015-1-9 |     658.50 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JF123456A  |
            | Application Raised Date | 2015-05-12 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | NOT_ENOUGH_RECORDS |
            | Assessment start date     | 2014-11-11         |
            | Application Raised date   | 2015-05-12         |
            | National Insurance Number | JF123456A          |
            | Threshold                 | 357.69             |
            | Employer Name             | Flying Pizza Ltd   |
