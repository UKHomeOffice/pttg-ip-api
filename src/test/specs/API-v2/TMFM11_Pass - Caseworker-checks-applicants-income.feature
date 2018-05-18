#@check-applicants-income
Feature: API returns a list of income for a specific NINO inorder to understand how much an Applicant has earned within a given period
  This feature of the Income Proving API allows a client to ask the question:

  “How much income has the applicant or spouse earned within a given period?"

#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (single job)
    Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
      | 2015-01-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-02-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-03-05 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-04-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-05-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-06-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-07-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-08-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-09-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-10-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-11-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-12-03 |    1666.11 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
    When the Income Proving v2 API is invoked with the following:
      | NINO      | SP123456A  |
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
      | National Insurance Number | SP123456A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 12 months (multiple jobs over year period)
    Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
      | 2015-01-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-02-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-03-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-04-11 |    3000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-05-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-06-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-07-13 |    2500.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-08-11 |    1000.00 | 1           |            | 123/SE45678       | Sheffield Spice |
      | 2015-09-11 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-10-13 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-11-11 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-12-11 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2016-01-11 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
    When the Income Proving v2 API is invoked with the following:
      | NINO      | SP654321A  |
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
      | National Insurance Number | SP654321A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (multiple jobs per month)
    Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
      | 2015-01-10 |    2000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-01-17 |    1000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-02-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-02-10 |    2000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-03-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-03-10 |    2000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-04-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-04-10 |    2000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-05-10 |    2000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-06-10 |    2000.00 | 1           |            | 123/HC45678       | Halifax PLC |
      | 2015-06-17 |    2000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
    When the Income Proving v2 API is invoked with the following:
      | NINO      | SP023987A  |
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
      | National Insurance Number | SP023987A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much he has earned within 6 months
    Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
      | 2015-01-04 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-02-04 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-05-20 |    2500.00 | 1           |            | 123/PD45678       | Pizza Hut LTD |
      | 2015-06-20 |    1666.00 | 1           |            | 123/PD45678       | Pizza Hut LTD |
      | 2015-07-20 |    1666.00 | 1           |            | 123/PD45678       | Pizza Hut LTD |
      | 2015-08-20 |    1666.00 | 1           |            | 123/PD45678       | Pizza Hut LTD |
    When the Income Proving v2 API is invoked with the following:
      | NINO      | SP987654A  |
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
      | National Insurance Number | SP987654A |


#@ Changed scenario, added Your search box
  Scenario: Robert obtains NINO income details to understand how much he has earned within 12 months
  Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
      | 2015-02-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-03-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-04-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-05-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-06-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-07-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-08-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-09-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-10-01 |    1666.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-11-01 |    1500.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2015-12-01 |    1000.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
      | 2016-01-01 |    2500.00 | 1           |            | 123/FD45678       | Flying Pizza Ltd |
    When the Income Proving v2 API is invoked with the following:
      | NINO      | SP765432A  |
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
      | National Insurance Number | SP765432A |
