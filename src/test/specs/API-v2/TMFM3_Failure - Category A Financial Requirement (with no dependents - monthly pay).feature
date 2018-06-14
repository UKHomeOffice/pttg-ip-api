Feature: Failure - Category A Financial Requirement - with no dependents - monthly pay

    Requirement to meet Category A
    Applicant or Sponsor has received < 6 consecutive monthly payments from the same employer over the 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Gross Monthly Income is < £1550.00 in any one of the 6 payments in the 182 days prior to the Application Raised Date


#New Scenario -
    Scenario: Jill does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    Pay date 15th of the month
    Before day of Application Raised Date
    She earns £1000 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference | Employer        |
            | 2015-01-15 | 1000.00 |            | 06           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-15 | 1000.00 |            | 05           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-15 | 1000.00 |            | 04           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-15 | 1000.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-15 | 1000.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-08-15 | 1000.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | JL123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-07-17                    |
            | Application Raised date   | 2015-01-15                    |
            | National Insurance Number | JL123456A                     |
            | Threshold                 | 1550.00                       |
            | Employer Name             | Flying Pizza Ltd              |

###New Scenario -
    Scenario: Francois does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    Pay date 28th of the month
    After day of Application Raised Date
    He earns £1250 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference| Employer         |
            | 2015-02-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-01-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-12-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-11-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-10-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |
            | 2014-09-28 | 1250.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | AP123456B  |
            | Application Raised Date | 2015-03-28 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | MONTHLY_VALUE_BELOW_THRESHOLD |
            | Assessment start date     | 2014-09-27                    |
            | Application Raised date   | 2015-03-28                    |
            | National Insurance Number | AP123456B                     |
            | Threshold                 | 1550.00                       |
            | Employer Name             | Flying Pizza Ltd              |

#New Scenario -
    Scenario: Kumar does not meet the Category A employment duration Requirement (He has worked for his current employer for only 3 months)

    Pay date 3rd of the month
    On same day of Application Raised Date
    He earns £1600 Monthly Gross Income BUT for only 3 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference| Employer         |
            | 2015-06-03 | 1600.00 |            | 03           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-03 | 1600.00 |            | 02           | FP/Ref1       | Flying Pizza Ltd |
            | 2015-04-03 | 1600.00 |            | 01           | FP/Ref1       | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | KS123456C  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | NOT_ENOUGH_RECORDS |
            | Assessment start date     | 2015-01-02         |
            | Application Raised date   | 2015-07-03         |
            | National Insurance Number | KS123456C          |
            | Threshold                 | 1550.00            |
            | Employer Name             | Flying Pizza Ltd   |

#New scenario - Added on 25th July 2017
    Scenario: Alan Partridge does not meet the Category A employment duration Requirement
    (Despite passing the financial threshold he has only worked for his current employer for 2 months)

    Pay date 28th of the month
    He has received 6 Monthly Gross Income payments of £1600.00 in the 182 day period from two employers
    He worked for a different employer before his current employer

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number   | PAYE Reference | Employer            |
            | 2015-08-28 | 1600.00 |             |      06        | FP/Ref2        | Curry House Ltd     |
            | 2015-07-28 | 1600.00 |             |      05        | FP/Ref2        | Curry House Ltd     |
            | 2015-06-28 | 1600.00 |             |      04        | FP/Ref1        | Office Supplies Ltd |
            | 2015-05-28 | 1600.00 |             |      03        | FP/Ref1        | Office Supplies Ltd |
            | 2015-04-28 | 1600.00 |             |      02        | FP/Ref1        | Office Supplies Ltd |
            | 2015-03-28 | 1600.00 |             |      01        | FP/Ref1        | Office Supplies Ltd |

    When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | GG987654A  |
            | Application Raised Date | 2015-09-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                |
            | Financial requirement met | false              |
            | Failure reason            | MULTIPLE_EMPLOYERS |
            | Assessment start date     | 2015-03-05         |
            | Application Raised date   | 2015-09-03         |
            | National Insurance Number | GG987654A          |
            | Threshold                 | 1550.00           |
            | Employer Name             | Curry House Ltd    |

#New scenario - Added on 25th July 2017
    Scenario: David Jones does not meet the Category A employment duration Requirement (Despite passing the financial threshold he has two current active employers)

    Pay date is 28th of the month
    He has received 12 Monthly Gross Income payments of £900.00 and £1200.00 in the 182 day period from two employers

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number   | PAYE Reference | Employer            |
            | 2015-08-28 | 1200.00 |             |      06        | FP/Ref2        | Curry House Ltd     |
            | 2015-08-28 |  900.00 |             |      06        | FP/Ref1        | Johns Chippy        |
            | 2015-07-28 | 1200.00 |             |      05        | FP/Ref2        | Curry House Ltd     |
            | 2015-07-28 |  900.00 |             |      05        | FP/Ref1        | Johns Chippy        |
            | 2015-06-28 | 1200.00 |             |      04        | FP/Ref2        | Curry House Ltd     |
            | 2015-06-28 |  900.00 |             |      04        | FP/Ref1        | Johns Chippy        |
            | 2015-05-28 | 1200.00 |             |      03        | FP/Ref2        | Curry House Ltd     |
            | 2015-05-28 |  900.00 |             |      03        | FP/Ref1        | Johns Chippy        |
            | 2015-04-28 | 1200.00 |             |      02        | FP/Ref2        | Curry House Ltd     |
            | 2015-04-28 |  900.00 |             |      02        | FP/Ref1        | Johns Chippy        |
            | 2015-03-28 | 1200.00 |             |      01        | FP/Ref2        | Curry House Ltd     |
            | 2015-03-28 |  900.00 |             |      01        | FP/Ref1        | Johns Chippy        |

    When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | AA987654A  |
            | Application Raised Date | 2015-09-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                                |
            | Financial requirement met | false                              |
            | Failure reason            | NON_CONSECUTIVE_MONTHS             |
            | Assessment start date     | 2015-03-05                         |
            | Application Raised date   | 2015-09-03                         |
            | National Insurance Number | AA987654A                          |
            | Threshold                 | 1550.00                            |
            | Employer Name             | Curry House  Ltd                   |
            | Employer Name             | Johns Chippy                       |

#New scenario - Added on 24th July 2017
    Scenario: Benedict Smythe does not meet the Category A Employment Payment Frequency Requirement
    (He passes the Cat A financial threshold & he has worked for the same employer but his payment frequency has changed)

    He was received 5 Monthly Gross income payments of £1400.00 and then 4 Weekly Gross income payments of £200.00 in the 182 period from the same employer

        Given HMRC has the following income records:
            | Date       | Amount   | Week Number | Month Number | PAYE Reference| Employer         |
            | 2015-10-30 | 200.00   |     04      |              | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-23 | 200.00   |     03      |              | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-16 | 200.00   |     02      |              | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-09 | 200.00   |     01      |              | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-29 | 1400.00  |             |     05       | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-29 | 1400.00  |             |     04       | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-29 | 1400.00  |             |     03       | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-29 | 1400.00  |             |     02       | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-29 | 1400.00  |             |     01       | FP/Ref1       | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | JW984624A  |
            | Application Raised Date | 2015-11-22 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                  |
            | Financial requirement met | false                |
            | Failure reason            | PAY_FREQUENCY_CHANGE |
            | Assessment start date     | 2015-05-24           |
            | Application Raised date   | 2015-11-22           |
            | National Insurance Number | JW984624A            |
            | Threshold                 | 0                    |
            | Employer Name             | Flying Pizza Ltd     |

######## New scenario 27th July

    Scenario: Jill does not meet the Category A Financial Requirement.
    She received a one off payment which is above the required total threshold amount but does not meet the monthly threshold criteria
    (She has earned < the Cat A financial threshold)

    Pay date 15th of the month
    Before day of Application Raised Date
    She earns £1000 Monthly Gross Income EVERY of the 6 months

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference | Employer        |

            | 2014-11-15 | 9600.00 |            | 04           |               | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | JL123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | NOT_ENOUGH_RECORDS            |
            | Assessment start date     | 2014-07-17                    |
            | Application Raised date   | 2015-01-15                    |
            | National Insurance Number | JL123456A                     |
            | Threshold                 | 1550.00                       |
            | Employer Name             | Flying Pizza Ltd              |


####### New scenario - 31st July

    Scenario: Jill does not meet the Category A Financial Requirement.
    No records could be found for Jill


        Given HMRC has the following income records:
            | Date       | Amount  | Week Number| Month Number | PAYE Reference | Employer        |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO                    | JL123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                           |
            | Financial requirement met | false                         |
            | Failure reason            | NOT_ENOUGH_RECORDS            |
            | Assessment start date     | 2014-07-17                    |
            | Application Raised date   | 2015-01-15                    |
            | National Insurance Number | JL123456A                     |
