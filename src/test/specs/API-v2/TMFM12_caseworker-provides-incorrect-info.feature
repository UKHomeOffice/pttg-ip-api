#@caseworker-provides-incorrect-info
Feature: Robert is presented with an error when attempting to obtain a NINOs income information

  Scenario: Robert is unable to obtain the NINOs income details due to NOT providing the NINO field
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      |            |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                                                      |
      | Status code    | 0004                                                     |
      | Status message | Error: Invalid NINO |


  Scenario: Robert is unable to obtain the NINOs income details due to providing a 7 character NINO
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | QQ1236A    |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                           |
      | Status code    | 0004                          |
      | Status message | Error: Invalid NINO |

  Scenario: Robert is unable to obtain the NINOs income details due to NOT providing the From Date field
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP123456A  |
      | From Date |            |
      | To Date   | 2015-06-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                                   |
      | Status code    | 0004                                  |
      | Status message | Error: From date is invalid |

  Scenario: Robert is unable to obtain the NINOs income details due to NOT providing the To Date field
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP129856A  |
      | From Date | 2015-01-01 |
      | To Date   |            |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                                 |
      | Status code    | 0004                                |
      | Status message | Error: To date is invalid |

  Scenario: Robert is unable to obtain the NINOs income details due to NINO does not exist being held by the HMRC for the give NINO
    Given HMRC has no matching record
    When the Income Proving v2 API is invoked with the following:
      | nino      | PP129435A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 404                |
      | Status code    | 0009               |
      | Status message | Resource not found |

  Scenario: Robert is unable to obtain the NINOs income details due to no income records being held by the HMRC for the give NINO
    Given HMRC has the following income records:
      | Date       | Amount  | Week Number| Month Number| PAYE Reference| Employer         |
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP769875A  |
      | From Date | 2015-01-01 |
      | To Date   | 2015-06-30 |
    Then The API provides the following Individual details:
      | HTTP Status | 200 |


  Scenario: Robert is unable to obtain the NINOs income details due to a future From Date and To Date
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP129856A  |
      | From Date | 2019-06-30 |
      | To Date   | 2019-12-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                       |
      | Status code    | 0004                      |
      | Status message | Error: fromDate |

  Scenario: Robert is unable to obtain the NINOs income details due to a future From Date
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP129856A  |
      | From Date | 2019-06-30 |
      | To Date   | 2015-12-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                       |
      | Status code    | 0004                      |
      | Status message | Error: fromDate |

  Scenario: Robert is unable to obtain the NINOs income details due to a future To Date
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving v2 API is invoked with the following:
      | nino      | SP129856A  |
      | From Date | 2015-06-30 |
      | To Date   | 2019-12-30 |
    Then The API provides the following Individual details:
      | HTTP Status    | 400                     |
      | Status code    | 0004                    |
      | Status message | Error: toDate |
