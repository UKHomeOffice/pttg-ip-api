Feature: Category B Financial Requirement - Solo & Combined Applications for Non-Salaried Assessments

    # JIRA STORIES: EE-3839 (Employment Check) & EE-3841 (Category B - Non-Salaried)
    # Scenarios where Category B Non-Salaried Assessments pass
    # Employment check threshold is £1550 within the immediate 32 days from and inclusive of application raised date
    # Annual threshold is £18,600 if no dependents are included. Addition of a first dependent child adds £3,800 then each thereafter a further £2,400.
    # Formula for calculating the income for comparison against the threshold is: Total all payments, divide by number of months paid, multiply by 12

    Scenario: 01: Georgina has no dependents. Her income history shows a payment that meets the employment check in the immediate 32 days and payments in all the 12 months prior that meet the annual threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 |  550.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1050.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 3000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | GH345678S  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-30       |
            | National Insurance Number | GH345678S        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


#        CONTINUE FROM HERE











    Scenario: 02: David has no dependents. His income history shows no payment in the ARD month but payments in all 6 months prior that meet the threshold

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 03: Pauline has no dependents. Her income history shows no payment in the ARD month but payments in all 6 months prior that meet the threshold, but ignoring payments from a second employer

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-03-29 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 2000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1000.00 | FP/Ref2        | Flowers 4U Ltd   |
            | 2017-11-30 | 2300.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200                              |
            | Category                  | A                                |
            | Financial requirement met | true                             |
            | Application Raised date   | 2018-04-31                       |
            | National Insurance Number | AA345678A                        |
            | Threshold                 | 18600                            |
            | Employer Name             | Flying Pizza Ltd, Flowers 4U Ltd |


    Scenario: 04: Sarah has no dependants. Her income history shows the ARD month with a payment that meets the threshold in the ARD month. All other months are blank.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-12-22 | 9300.00 | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-30       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 05: Sally has no dependants. Her income history shows the ARD month with a payment that meets the threshold at the very end of the 6 month range. All other months are blank.

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2017-06-30 | 9300.00 | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2017-12-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-12-30       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 06: Phillip has no dependents. His income history shows the all 6 months with payments. One month has weekly payments and another month has fortnightly and a monthly payment. Average meets the threshold.

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
            | NINO                    | AA345678A  |
            | Application Raised Date | 2017-09-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2017-09-30       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 07: Siobhan has one dependent. Her income history shows a payment in the ARD month and the other 5 months with a mixture of payments and gaps that meet the threshold (Nov & Sept are blank)

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-31 |  200.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-01-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | £22400           |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 08: Derek has two dependents. His income history shows a payment in the ARD month and the other 5 months with a mixture of payments and gaps that meet the threshold (Nov, Oct & Sept are blank)

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-01-26 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 1800.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-08 |  600.00 | FP/Ref2        | Flying Pizza Ltd |
            | 2017-08-31 | 5000.00 | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-01-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | £24800           |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 09: Geraldine has no dependents. Her income history shows a payment in the ARD month and remaining months but does not meet the threshold until it is supplemented by a partners income

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
            | 2015-01-23 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2014-12-23 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2014-11-22 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2014-10-23 |  100.00 | HO/Ref9        | The Home Office  |
            | 2014-09-25 |  100.00 | HO/Ref9        | The Home Office  |
            | 2014-08-23 |  100.00 | HO/Ref9        | The Home Office  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |


    Scenario: 10: Bertie has no dependents. His income history shows a payment in the ARD month and remaining months have gaps but do not meet the threshold until it is supplemented by a partners income also having gaps

        Given HMRC has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2018-04-27 | 2449.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 |  550.50 | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1000.00 | FP/Ref1        | Flying Pizza Ltd |

        And the applicants partner has the following income records:
            | Date       | Amount  | PAYE Reference | Employer         |
            | 2015-01-23 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2014-12-23 | 1000.00 | HO/Ref9        | The Home Office  |
            | 2014-11-22 | 2000.00 | HO/Ref9        | The Home Office  |
            | 2014-10-23 |  300.00 | HO/Ref9        | The Home Office  |

        When the Income Proving v2 TM Family API is invoked with the following:
            | NINO                    | AA345678A  |
            | Application Raised Date | 2018-04-31 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status               | 200              |
            | Category                  | A                |
            | Financial requirement met | true             |
            | Application Raised date   | 2018-04-31       |
            | National Insurance Number | AA345678A        |
            | Threshold                 | 18600            |
            | Employer Name             | Flying Pizza Ltd |
