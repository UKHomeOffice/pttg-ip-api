Feature: Category A Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORY: EE-3838
    # Scenarios where Category A Non-Salaried Assessments pass
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by 6, multiply by 12


    # BACKGROUND: Applications with one applicant will be required to meet a main threshold value of £18,600.
    #             Applications with one dependant will be required to meet an amended threshold value of £22,400
    #             Applications with two dependants will be required to meet a further amended threshold value of £24,800


    Scenario 01: Lucy has no dependents. Her income history shows a payment in the ARD month and payments in all the 5 months prior that meet the threshold


        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

-------------

    Scenario 02: David has no dependents. His income history shows no payment in the ARD month but payments in all 6 months prior that meet the threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | FD345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | FD345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

-------------

    Scenario 03: Pauline has no dependents. Her income history shows no payment in the ARD month but payments in all 6 months prior that meet the threshold, but ignoring payments from a second employer

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 | FP/Ref2        | Flowers 4U Ltd   |
            | 2017-11-30 | 2300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | VB345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                              |
            | Category                  | A                                |
            | Financial requirement met | true                             |
            | Application Raised date   | 2018-04-30                       |
            | National Insurance Number | VB345678A                        |
            | Threshold                 | 18600                            |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd |   #maybe change this pending what it is used for

-------------

    Scenario 04: Sarah has no dependants. Her income history shows the ARD month with a payment that meets the threshold in the ARD month. All other months are blank.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-12-22 | 9300.00 | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | JH573849A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-30       |
            | National Insurance Number | JH573849A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

--------------

    Scenario 05: Sally has no dependants. Her income history shows the ARD month with a payment that meets the threshold at the very end of the 6 month range. All other months are blank.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-06-01 | 9300.00 | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | KL927581A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-30       |
            | National Insurance Number | KL927581A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

--------------

    Scenario 06: Phillip has no dependents. His income history shows the all 6 months with payments. One month has weekly payments and another month has fortnightly and a monthly payment. Average meets the threshold.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 |  325.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-06-23 |  325.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-06-16 |  325.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-06-09 |  325.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-05-26 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-28 |  500.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-07 |  500.00 | FP/Ref2        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AB889357A  |
            | Application Raised Date | 2017-09-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-09-30       |
            | National Insurance Number | AAB889357A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |

---------------

    Scenario 07: Siobhan has one dependent. Her income history shows a payment in the ARD month and the other 5 months with a mixture of payments and gaps that meet the threshold (Nov & Sept are blank)

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-31 |  200.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | LA345628A  |
            | Application Raised Date | 2018-01-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | LA345628A        |
            | Threshold                 | £22400           |
            | Employer Name             | Flying Pizza Ltd |

----------------

    Scenario 08: Derek has two dependents. His income history shows a payment in the ARD month and the other 5 months with a mixture of payments and gaps that meet the threshold (Nov, Oct & Sept are blank)

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 1800.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-08 |  600.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-08-31 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | PL327678A  |
            | Application Raised Date | 2018-01-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | PL327678A        |
            | Threshold                 | £24800           |
            | Employer Name             | Flying Pizza Ltd |

----------------

    Scenario 09: Geraldine has no dependents. Her income history shows a payment in the ARD month and remaining months but does not meet the threshold until it is supplemented by a partners income

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-29 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-26 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2018-03-28 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2018-02-28 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2018-01-31 |  100.00 | HO/Ref9        | The Home Office  |
            | 2017-12-27 |  100.00 | HO/Ref9        | The Home Office  |
            | 2017-11-26 |  100.00 | HO/Ref9        | The Home Office  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | QS317678A  |
            | NINO - Partner          | GF374820B  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                               |
            | Category                  | A                                 |
            | Financial requirement met | true                              |
            | Application Raised date   | 2018-04-31                        |
            | National Insurance Number | QS317678A                         |
            | National Insurance Number | GF374820B                         |
            | Threshold                 | 18600                             |
            | Employer Name             | Flying Pizza Ltd, The Home Office |


--------------

    Scenario 10: Bertie has no dependents. His income history shows a payment in the ARD month and remaining months have gaps but do not meet the threshold until it is supplemented by a partners income also having gaps

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 2449.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  550.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-28 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2018-02-28 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2018-01-31 | 2000.00 | HO/Ref9        | The Home Office  |
            | 2017-11-28 |  300.00 | HO/Ref9        | The Home Office  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO - Applicant        | JD345678A  |
            | NINO - Partner          | GH428174C  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                               |
            | Category                  | A                                 |
            | Financial requirement met | true                              |
            | Application Raised date   | 2018-04-31                        |
            | National Insurance Number | JD345678A                         |
            | National Insurance Number | GH428174C                         |
            | Threshold                 | 18600                             |
            | Employer Name             | Flying Pizza Ltd, The Home Office |
