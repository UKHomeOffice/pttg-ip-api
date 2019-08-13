Feature: Re-run various tests with different thresholds to prove that thresholds are configurable

    Background: Thresholds are configured to non-default values
        Given The yearly threshold is configured to 9300:
        And The single dependant yearly threshold is configured to 11200:
        And The remaining dependants increment is configured to 1200:


    Scenario: Category A Single applicant with no dependants and 6 consecutive monthly payments over threshold

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 500.00  |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1000.00 |             | 02           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-30 | 1000.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-30 | 1000.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 650.00  |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-30 | 500.00  |             | 10           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-04-30       |
            | Category A Non Salaried | Assessment Start date     | 2017-10-30       |
            | Category A Non Salaried | Threshold                 | 9300             |
            | Category A Non Salaried | Employer Name - AA345678A | Flying Pizza Ltd |


    Scenario: Category A Single applicant with one dependant and gaps withing monthly payments but over threshold

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-01-26 | 2500.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 2500.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 500.00  |             | 10           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-31 | 100.00  |             | 07           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | LA345628A  |
            | Application Raised Date | 2018-01-31 |
            | Dependants              | 1          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-01-31       |
            | Category A Non Salaried | Assessment Start date     | 2017-07-31       |
            | Applicant               | National Insurance Number | LA345628A        |
            | Category A Non Salaried | Threshold                 | 11200            |
            | Category A Non Salaried | Employer Name - LA345628A | Flying Pizza Ltd |


    Scenario: Category A Applicant has two dependants with mixed frequency payments with gaps but over threshold

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-01-26 | 2500.00 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-22 | 900.00  |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-08 | 300.00  | 38          |              | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-30 | 2500.00 |             | 09           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | PL327678A  |
            | Application Raised Date | 2018-03-31 |
            | Dependants              | 2          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Category A Non Salaried | Financial requirement met | true             |
            | Category A Non Salaried | Application Raised date   | 2018-03-31       |
            | Category A Non Salaried | Assessment Start date     | 2017-09-30       |
            | Applicant               | National Insurance Number | PL327678A        |
            | Category A Non Salaried | Threshold                 | 12400            |
            | Category A Non Salaried | Employer Name - PL327678A | Flying Pizza Ltd |


    Scenario: Category B Three dependents. Employment check met. Annual check met.

        Given HMRC has the following income records:
            | Date       | Amount  | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-04-09 | 1133.33 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-03-30 | 1133.33 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-02-28 | 1133.33 |             | 11           | FP/Ref1        | Flying Pizza Ltd |
            | 2018-01-31 | 1133.33 |             | 09           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-12-29 | 1133.33 |             | 07           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-11-30 | 1133.33 |             | 05           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-10-27 | 1133.33 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-09-29 | 1133.33 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-08-25 | 1133.33 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-07-28 | 1133.33 |             | 04           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-06-30 | 1133.33 |             | 03           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-05-26 | 1133.37 |             | 02           | FP/Ref1        | Flying Pizza Ltd |

        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |
            | Dependants              | 3          |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200              |
            | Applicant               | National Insurance Number | AA345678A        |
            | Category B non salaried | Financial requirement met | true             |
            | Category B non salaried | Application Raised date   | 2018-04-30       |
            | Category B non salaried | Assessment Start Date     | 2017-04-30       |
            | Category B non salaried | Threshold                 | 13600            |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd |


    Scenario: Category B No dependents. Employment check met. Annual check not met, boundary test.

        Given HMRC has the following income records:
            | Date       | Amount | Week Number | Month Number | PAYE Reference | Employer         |
            | 2018-03-30 | 775.00 |             | 12           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-30 | 774.98 |             | 01           | FP/Ref1        | Flying Pizza Ltd |
            | 2017-04-29 | 00.01  |             | 01           | FP/Ref1        | Flying Pizza Ltd |


        When the Income Proving v3 TM Family API is invoked with the following:
            | NINO - Applicant        | AA345678A  |
            | Application Raised Date | 2018-04-30 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Response           | HTTP Status               | 200                               |
            | Applicant               | National Insurance Number | AA345678A                         |
            | Category B non salaried | Financial requirement met | false                             |
            | Category B non salaried | Failure Reason            | CATB_NON_SALARIED_BELOW_THRESHOLD |
            | Category B non salaried | Application Raised date   | 2018-04-30                        |
            | Category B non salaried | Assessment Start Date     | 2017-04-30                        |
            | Category B non salaried | Threshold                 | 9300                              |
            | Category B non salaried | Employer Name - AA345678A | Flying Pizza Ltd                  |
