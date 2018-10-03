Feature: Category A Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORY: EE-3838
    # Scenarios where Category A Non-Salaried Assessments pass
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by 6, multiply by 12


    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800


    # 01 Lucy has no dependents. Her income history shows a payments in 6 months that meet the threshold. Assessment range 2018-04-30 to 2017-10-30
    Scenario: No dependants. 6 monthly payments over threshold.


        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1300.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30       |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd |

############

    # 02 David has no dependents. His income history shows payments in 7 months prior that meet the threshold. Assessment range 2018-04-30 to 2017-10-30
    Scenario: No dependants. 7 monthly payments over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 2000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1300.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 500.00  |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 500.00  |             | 07           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | GE345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30       |
            | Applicant               | National Insurance Number | GE345678A        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - GE345678A | Flying Pizza Ltd |

############

    # 03 Pauline has no dependents. Her income history shows payments in 6 months that meet the threshold, but ignoring payments from a second employer.  Assessment range 2018-04-30 to 2017-10-30
    Scenario: No dependants. 6 months of payment over threshold, ignoring second employer.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-30 | 500.00  |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 |             | 09           | FP/Ref2        | Flowers 4U Ltd   |
            | 2017-11-30 | 2300.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 500.00  |             | 07           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | EB345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                              |
            | Category A Non Salaried | Financial requirement met | true                             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30                       |
            | Applicant               | National Insurance Number | EB345678A                        |
            | Category A Non Salaried | Threshold                 | 18600                            |
            | Category A Non Salaried | Employer Name - EB345678A | Flying Pizza Ltd, Flowers 4U Ltd |

############

    # 04 Sarah has no dependants. Her income history shows a payment that meets the threshold at the very beginning of the 6 month range. All other months are blank. Assessment range 2017-12-30 to 2017-07-01.
    Scenario: No dependants. Single payment over threshold at very beginning of 6 month assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2017-12-30 | 9300.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JH573849A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2017-12-30       |
            | Applicant               | National Insurance Number | JH573849A        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - JH573849A | Flying Pizza Ltd |

############

    # 05 Sally has no dependants. Her income history shows a payment that meets the threshold at the very end of the 6 month range. All other months are blank. Assessment range 2017-12-30 to 2017-07-01.
    Scenario: No dependants. Single payment over threshold at very end of 6 month assessment range.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2017-07-01 | 9300.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | KL927581A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2017-12-30       |
            | Applicant               | National Insurance Number | KL927581A        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - KL927581A | Flying Pizza Ltd |

############

    # 06 Phillip has no dependents. His income history shows 6 months with payments. One month has weekly payments and another month has fortnightly and a monthly payment. Average meets the threshold. Assessment range 2017-09-30 to 2017-04-01.
    Scenario: No dependants. Mixed frequency payments in last 6 month over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2017-09-29 | 2000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 325.00  |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-23 | 325.00  |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-16 | 325.00  |             | 08           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-09 | 325.00  |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1000.00 |             | 06           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-28 | 500.00  |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-07 | 500.00  | 03          |              | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AB889357A  |
            | Application Raised Date | 2017-09-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2017-09-30       |
            | Applicant               | National Insurance Number | AB889357A        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - AB889357A | Flying Pizza Ltd |

############

    # 07 Siobhan has one dependent. Her income history shows a payment in 6 months with a mixture of payments and gaps that meet the threshold (Nov & Sept are blank). Assessment range 2018-01-31 to 2017-08-02.
    Scenario: One dependant. Payments with monthly gaps but over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 5000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-31 | 200.00  |             | 05           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | LA345628A  |
            | Application Raised Date | 2018-01-31 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-01-31       |
            | Applicant               | National Insurance Number | LA345628A        |
            | Category A Non Salaried | Threshold                 | 22400            |
            | Category A Non Salaried | Employer Name - LA345628A | Flying Pizza Ltd |

############

    #  08 Derek has two dependents. His income history shows a payment in 6 months with a mixture of payments and gaps that meet the threshold (Nov, Oct & Sept are blank). Assessment range 2017-01-31 to 2017-08-02.
    Scenario: Two dependants. Mixed frequency payments with gaps but over threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 1800.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-08 | 600.00  | 38          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-31 | 5000.00 |             | 05           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL327678A  |
            | Application Raised Date | 2018-01-31 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-01-31       |
            | Applicant               | National Insurance Number | PL327678A        |
            | Category A Non Salaried | Threshold                 | 24800            |
            | Category A Non Salaried | Employer Name - PL327678A | Flying Pizza Ltd |

############

    #  09 Geraldine has no dependents. Her income history shows payments in 6 months but does not meet the threshold until it is supplemented by a partners income. Assessment range 2018-04-30 to 2017-10-30.
    Scenario: No dependants. Applicant income below threshold. Over threshold when combined with partner income.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 1000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1000.00 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-04-26 | 1000.00 |             | 01           | HO/Ref9        | The Home Office |
            | 2018-03-28 | 1000.00 |             | 12           | HO/Ref9        | The Home Office |
            | 2018-02-28 | 1000.00 |             | 11           | HO/Ref9        | The Home Office |
            | 2018-01-31 | 100.00  |             | 10           | HO/Ref9        | The Home Office |
            | 2017-12-27 | 100.00  |             | 09           | HO/Ref9        | The Home Office |
            | 2017-11-26 | 100.00  |             | 08           | HO/Ref9        | The Home Office |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | SS317678A  |
            | NINO - Partner          | GG374820B  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30       |
            | Applicant               | National Insurance Number | SS317678A        |
            | Partner                 | National Insurance Number | GG374820B        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - SS317678A | Flying Pizza Ltd |
            | Category A Non Salaried | Employer Name - GG374820B | The Home Office  |


############

    # 10 Bertie has no dependents. His income history shows payments but with some months having gaps. The payments do not meet the threshold until it is supplemented by a partners income also having gaps. Assessment range 2018-04-30 to 2017-10-30.
    Scenario: No dependants. Payments with gaps, below threshold. Over threshold when adding partner payment, also with gaps.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-27 | 2449.50 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 550.50  |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 |             | 08           | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer        |
            | 2018-03-28 | 1000.00 |             | 12           | HO/Ref9        | The Home Office |
            | 2018-02-28 | 1000.00 |             | 11           | HO/Ref9        | The Home Office |
            | 2018-01-31 | 2000.00 |             | 10           | HO/Ref9        | The Home Office |
            | 2017-11-28 | 300.00  |             | 08           | HO/Ref9        | The Home Office |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | JR345678A  |
            | NINO - Partner          | GH428174C  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30       |
            | Applicant               | National Insurance Number | JR345678A        |
            | Partner                 | National Insurance Number | GH428174C        |
            | Category A Non Salaried | Threshold                 | 18600            |
            | Category A Non Salaried | Employer Name - JR345678A | Flying Pizza Ltd |
            | Category A Non Salaried | Employer Name - GH428174C | The Home Office  |
