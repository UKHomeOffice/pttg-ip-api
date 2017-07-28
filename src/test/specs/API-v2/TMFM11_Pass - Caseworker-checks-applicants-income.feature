#@check-applicants-income
Feature: API returns a list of income for a specific NINO inorder to understand how much an Applicant has earned within a given period.
  This feature of the Income Proving API allows a client to ask the question:

  “How much income has the applicant or spouse earned within a given period?"

#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (single job)
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ123456A  |
      | From Date | 2015-01-01 | (should be most recent) (application raised date)
      | To Date   | 2015-06-30 | (Assesment start date)

    Then The API provides the following result:
      | Date       | Amount  | Week Number |Month Number| PAYE Reference  | Employer         |
      | 2015-06-03 | 1666.11 |             |    6       | FP/Ref1         |  Flying Pizza Ltd|
      | 2015-05-03 | 1666.11 |             |    5       | FP/Ref1         |  Flying Pizza Ltd|
      | 2015-04-05 | 1666.11 |             |    4       | FP/Ref1         |  Flying Pizza Ltd|
      | 2015-03-03 | 1666.11 |             |    3       | FP/Ref1         |  Flying Pizza Ltd|
      | 2015-02-03 | 1666.11 |             |    2       | FP/Ref1         |  Flying Pizza Ltd|
      | 2015-01-03 | 1666.11 |             |    1       | FP/Ref1         |  Flying Pizza Ltd|

      | Total:     | 9996.66 |

    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ123456A |

########New Scenario 28th July

  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (single job)
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ123456A  |
      | From Date | 2015-11-03 |(Application raised date)
      | To Date   | 2015-05-05 |(Assesment Start date)

    Then The API provides the following result:
            | Date       | Amount | Week Number | Month Number| PAYE Reference| Employer        |
            | 2015-10-30 | 387.50 |    26       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-23 | 387.50 |    25       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-16 | 387.50 |    24       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-09 | 387.50 |    23       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-02 | 387.50 |    22       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-25 | 387.50 |    21       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-18 | 387.50 |    20       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-11 | 387.50 |    19       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-04 | 387.50 |    18       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-28 | 387.50 |    17       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-21 | 387.50 |    16       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-14 | 387.50 |    15       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-07 | 387.50 |    14       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-31 | 387.50 |    13       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-24 | 387.50 |    12       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-17 | 387.50 |    11       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-10 | 387.50 |    10       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-03 | 387.50 |    09       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-26 | 387.50 |    08       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-19 | 387.50 |    07       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-12 | 387.50 |    06       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-05 | 387.50 |    05       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-29 | 387.50 |    04       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-22 | 387.50 |    03       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-15 | 387.50 |    02       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-05-08 | 387.50 |    01       |            | FP/Ref1       | Flying Pizza Ltd |

            | Total:     | 10075.00 |

    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ123456A |

####@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 12 months (multiple jobs over year period)
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ654321A  |
      | From Date | 2015-02-11 | (Application Raised Date)
      | To Date   | 2016-01-15 | (Assesment start date)
    Then The API provides the following result:
      | Date       | Amount    | Week Number | Month Number| PAYE Reference| Employer          |
      | 2015-02-11 |  1000.00  |             |  12         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-03-11 |  1000.00  |             |  11         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-04-11 |  3000.00  |             |  10         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-05-11 |  1000.00  |             |   9         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-06-11 |  1000.00  |             |   8         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-07-13 |  2500.00  |             |   7         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-08-11 |  1000.00  |             |   6         |    SP/Ref1    |  Sheffield Spice  |
      | 2015-09-11 |  1666.00  |             |   5         |    FP/Ref1    |  Flying Pizza Ltd |
      | 2015-10-13 |  1666.00  |             |   4         |    FP/Ref1    |  Flying Pizza Ltd |
      | 2015-11-11 |  1666.00  |             |   3         |    FP/Ref1    |  Flying Pizza Ltd |
      | 2015-12-11 |  1666.00  |             |   2         |    FP/Ref1    |  Flying Pizza Ltd |
      | 2016-01-11 |  1666.00  |             |   1         |    FP/Ref1    |  Flying Pizza Ltd |
      | Total:     |                  | 18830.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ654321A |

######## New scenario July 28th

  Scenario: Robert obtains NINO income details to understand how much they have earned within 12 months (multiple jobs over year period)
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ654321A  |
      | From Date | 2015-01-01 |
      | To Date   | 2016-01-01 |

    Then The API provides the following result:
            | Date       | Amount | Week Number | Month Number| PAYE Reference| Employer        |
            | 2016-01-01 | 387.50 |    52       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-25 | 387.50 |    51       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-18 | 387.50 |    50       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-11 | 387.50 |    49       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-12-04 | 387.50 |    48       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-27 | 387.50 |    47       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-20 | 387.50 |    46       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-13 | 387.50 |    45       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-11-06 | 387.50 |    44       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-30 | 387.50 |    43       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-23 | 387.50 |    42       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-16 | 387.50 |    41       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-09 | 387.50 |    40       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-10-02 | 387.50 |    39       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-25 | 387.50 |    38       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-18 | 387.50 |    37       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-11 | 387.50 |    36       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-09-04 | 387.50 |    35       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-28 | 387.50 |    34       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-21 | 387.50 |    33       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-14 | 387.50 |    32       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-08-07 | 387.50 |    31       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-31 | 387.50 |    30       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-24 | 387.50 |    29       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-17 | 387.50 |    28       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-10 | 387.50 |    27       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-07-03 | 387.50 |    26       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-26 | 387.50 |    24       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-19 | 387.50 |    23       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-12 | 387.50 |    22       |            | FP/Ref1       | Flying Pizza Ltd |
            | 2015-06-05 | 387.50 |    11       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-05-29 | 387.50 |    10       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-05-22 | 387.50 |    21       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-05-15 | 387.50 |    20       |            | SP/Ref1       | Sheffield SPice  |
            | 2015-05-08 | 387.50 |    19       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-05-01 | 387.50 |    18       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-04-24 | 387.50 |    17       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-04-17 | 387.50 |    16       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-04-10 | 387.50 |    15       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-04-03 | 387.50 |    14       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-03-27 | 387.50 |    13       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-03-20 | 387.50 |    12       |            | SP/Ref1       | Sheffield Spicec |
            | 2015-03-13 | 387.50 |    11       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-03-06 | 387.50 |    10       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-02-27 | 387.50 |    09       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-02-20 | 387.50 |    08       |            | SP/Ref1       | Sheffield SPice  |
            | 2015-02-13 | 387.50 |    07       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-02-06 | 387.50 |    06       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-01-30 | 387.50 |    05       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-01-23 | 387.50 |    04       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-01-16 | 387.50 |    03       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-01-09 | 387.50 |    02       |            | SP/Ref1       | Sheffield Spice  |
            | 2015-01-02 | 387.50 |    01       |            | SP/Ref1       | Sheffield Spice  |
            | Total:     |                  | 20150.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ654321A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (multiple jobs per month)
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving API is invoked with the following:
      | NINO      | QQ023987A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following result:
      | 2015-01-10 | Flying Pizza Ltd | 2000.00  |
      | 2015-01-17 | Halifax PLC      | 1000.00  |
      | 2015-02-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-02-10 | Halifax PLC      | 2000.00  |
      | 2015-03-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-03-10 | Halifax PLC      | 2000.00  |
      | 2015-04-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-04-10 | Halifax PLC      | 2000.00  |
      | 2015-05-10 | Halifax PLC      | 2000.00  |
      | 2015-06-10 | Halifax PLC      | 2000.00  |
      | 2015-06-17 | Flying Pizza Ltd | 2000.00  |
      | Total:     |                  | 19998.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ023987A |



##### @ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much he has earned within 6 months
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving API is invoked with the following:
      | NINO      | QQ987654A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-08-30 |
    Then The API provides the following result:
      | 2015-01-04 | Flying Pizza Ltd | 1666.00  |
      | 2015-02-04 | Flying Pizza Ltd | 1666.00  |
      | 2015-05-20 | Pizza Hut LTD    | 2500.00  |
      | 2015-06-20 | Pizza Hut LTD    | 1666.00  |
      | 2015-07-20 | Pizza Hut LTD    | 1666.00  |
      | 2015-08-20 | Pizza Hut LTD    | 1666.00  |
      | Total:     |                  | 10830.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ987654A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much he has earned within 12 months
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving API is invoked with the following:
      | NINO      | QQ765432A  |
      | From Date | 2015-02-01 |
      | To Date   | 2016-01-31 |
    Then The API provides the following result:
      | 2015-02-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-03-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-04-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-05-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-06-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-07-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-08-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-09-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-10-01 | Flying Pizza Ltd | 1666.00  |
      | 2015-11-01 | Flying Pizza Ltd | 1500.00  |
      | 2015-12-01 | Flying Pizza Ltd | 1000.00  |
      | 2016-01-01 | Flying Pizza Ltd | 2500.00  |
      | Total:     |                  | 19994.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ765432A |

###### New scenario - 27 July

  Scenario: Robert obtains NINO income details to understand how much he has earned within 6 months
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ987654A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-08-30 |
    Then The API provides the following result:
      | 2015-01-04 | Flying Pizza Ltd | 1666.00  |
      | 2015-02-04 | Flying Pizza Ltd | 1666.00  |
      | 2015-05-20 | Pizza Hut LTD    | 2500.00  |
      | 2015-06-20 | Pizza Hut LTD    | 1666.00  |
      | 2015-07-20 | Pizza Hut LTD    | 1666.00  |
      | 2015-08-07 | Pizza Hut LTD    |  380.00  |
      | 2015-08-14 | Pizza Hut LTD    |  380.00  |
      | 2015-08-21 | Pizza Hut LTD    |  380.00  |
      | 2015-08-28 | Pizza Hut LTD    |  380.00  |

      | Total:     |                  | 10684.00 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ987654A |

####### New Scenario 27th July

  Scenario: Robert obtains NINO income details to understand how much he has earned within 6 months
    Given A service is consuming the Income Proving TM Family API v2

    When the Income Proving API is invoked with the following:
      | NINO      | QQ987654A  |
      | From Date | 2014-11-12 |
      | To Date   | 2015-05-12 |
    Then The API provides the following result:
            | Date       | Amount  | Week Number |Month Number| PAYE Reference  | Employer         |
            | 2015-04-24 | 2266.68 |             |     02     | FP/Ref1         | Flying Pizza Ltd |
            | 2015-03-27 | 2266.68 |             |     01     | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-27 | 525.00  |    16       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-20 | 525.00  |    15       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-13 | 525.00  |    14       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-02-06 | 525.00  |    13       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-30 | 525.00  |    12       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-23 | 525.00  |    11       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-16 | 525.00  |    10       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-09 | 525.00  |    09       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-01-02 | 525.00  |    08       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-26 | 525.00  |    07       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-19 | 525.00  |    06       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-12 | 525.00  |    05       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-12-05 | 525.00  |    04       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2015-11-28 | 525.00  |    03       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2014-11-21 | 525.00  |    02       |            | FP/Ref1         | Flying Pizza Ltd |
            | 2014-11-14 | 525.00  |    01       |            | FP/Ref1         | Flying Pizza Ltd |

              | Total:     |                  | 12933,36 |

    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ987654A |
