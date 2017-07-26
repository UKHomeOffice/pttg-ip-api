#@check-applicants-income
Feature: API returns a list of income for a specific NINO inorder to understand how much an Applicant has earned within a given period.
  This feature of the Income Proving API allows a client to ask the question:

  “How much income has the applicant or spouse earned within a given period?"

#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (single job)
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving API is invoked with the following:
      | NINO      | QQ123456A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following result:
      | 2015-01-03 | Flying Pizza Ltd | 1666.11 |
      | 2015-02-03 | Flying Pizza Ltd | 1666.11 |
      | 2015-03-05 | Flying Pizza Ltd | 1666.11 |
      | 2015-04-03 | Flying Pizza Ltd | 1666.11 |
      | 2015-05-03 | Flying Pizza Ltd | 1666.11 |
      | 2015-06-03 | Flying Pizza Ltd | 1666.11 |
      | Total:     |                  | 9996.66 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ123456A |

New Scenario######### 26th July 2017

  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months/26 weeks (single job)
    Given A service is consuming the Income Proving TM Family API v2
    When the Income Proving API is invoked with the following:
      | NINO      | QQ123456A  |
      | From Date | 2015-11-03 |
      | To Date   | 2015-05-05 |
    Then The API provides the following result:
         | Date       | Amount | Week Number | Month Number| PAYE Reference| Employer        |
         | 2015-10-30 | 416.52 |    26       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-10-23 | 416.52 |    25       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-10-16 | 416.52 |    24       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-10-09 | 416.52 |    23       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-10-02 | 416.52 |    22       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-09-25 | 416.52 |    21       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-09-18 | 416.52 |    20       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-09-11 | 416.52 |    19       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-09-04 | 416.52 |    18       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-08-28 | 416.52 |    17       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-08-21 | 416.52 |    16       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-08-14 | 416.52 |    15       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-08-07 | 416.52 |    14       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-07-31 | 416.52 |    13       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-07-24 | 416.52 |    12       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-07-17 | 416.52 |    11       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-07-10 | 416.52 |    10       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-07-03 | 416.52 |    09       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-06-26 | 416.52 |    08       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-06-19 | 416.52 |    07       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-06-12 | 416.52 |    06       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-06-05 | 416.52 |    05       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-05-29 | 416.52 |    04       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-05-22 | 416.52 |    03       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-05-15 | 416.52 |    02       |            | FP/Ref1       | Flying Pizza Ltd |
         | 2015-05-08 | 416.52 |    01       |            | FP/Ref1       | Flying Pizza Ltd |
      | Total:     |                  | 10829.52 |
    And The API provides the following Individual details:
      | HTTP Status               | 200       |
      | Individual title          | Mr        |
      | Individual forename       | Harry     |
      | Individual surname        | Callahan  |
      | National Insurance Number | QQ123456A |




#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 12 months (multiple jobs over year period)
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving API is invoked with the following:
      | NINO      | QQ654321A  |
      | From Date | 2015-02-11 |
      | To Date   | 2016-01-15 |
    Then The API provides the following result:
      | 2015-02-11 | Sheffield Spice  | 1000.00  |
      | 2015-03-11 | Sheffield Spice  | 1000.00  |
      | 2015-04-11 | Sheffield Spice  | 3000.00  |
      | 2015-05-11 | Sheffield Spice  | 1000.00  |
      | 2015-06-11 | Sheffield Spice  | 1000.00  |
      | 2015-07-13 | Sheffield Spice  | 2500.00  |
      | 2015-08-11 | Sheffield Spice  | 1000.00  |
      | 2015-09-11 | Flying Pizza Ltd | 1666.00  |
      | 2015-10-13 | Flying Pizza Ltd | 1666.00  |
      | 2015-11-11 | Flying Pizza Ltd | 1666.00  |
      | 2015-12-11 | Flying Pizza Ltd | 1666.00  |
      | 2016-01-11 | Flying Pizza Ltd | 1666.00  |
      | Total:     |                  | 18830.00 |
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


#@ Changed scenario, added Your search box
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
