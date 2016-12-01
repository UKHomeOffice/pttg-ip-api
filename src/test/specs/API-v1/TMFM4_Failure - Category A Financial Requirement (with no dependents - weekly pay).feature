Feature: Failure - Category A Financial Requirement (with no dependents - weekly pay)

    Requirement to meet Category A
    Applicant or Sponsor has received < 26 payments from the same employer over 182 day period prior to the Application Raised Date

    Financial employment income regulation to pass this Feature File
    Applicant or Sponsor has received 26 weekly Gross Income payments of < £357.69 in the 182 day period prior to the Application Raised Date

#New Scenario -
    Scenario: Davina Love does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    She earns £300.11 weekly Gross Income EVERY of the 26 weeks

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | DV123456A  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                          |
            | Financial requirement met  | false                        |
            | Failure reason             | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Individual title           | Miss                         |
            | Individual forename        | Davina                       |
            | Individual surname         | Love                         |
            | Application Raised to date | 2014-07-17                   |
            | Application Raised date    | 2015-01-15                   |
            | National Insurance Number  | DV123456A                    |
            | Threshold                  | 357.69                       |

#New Scenario -
    Scenario: Xavier Snow does not meet the Category A Financial Requirement (She has earned < the Cat A financial threshold)

    He earns £30.99 weekly Gross Income EVERY of the 26 weeks

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | XS123456B  |
            | Application Raised Date | 2015-12-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                          |
            | Financial requirement met  | false                        |
            | Failure reason             | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Individual title           | Mr                           |
            | Individual forename        | Xavier                       |
            | Individual surname         | Snow                         |
            | Application Raised to date | 2015-06-16                   |
            | Application Raised date    | 2015-12-15                   |
            | National Insurance Number  | XS123456B                    |
            | Threshold                  | 357.69                       |


#New Scenario -
    Scenario: Paul Young does not meet the Category A Financial Requirement (He has earned < the Cat A financial threshold)

    He earns £400.99 weekly Gross Income EVERY of the 24 weeks
    and he earns £300.99 weekly Gross Income for the LAST 2 weeks (total 26 weeks with the same employer)

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | PY123456B  |
            | Application Raised Date | 2015-01-15 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                          |
            | Financial requirement met  | false                        |
            | Failure reason             | WEEKLY_VALUE_BELOW_THRESHOLD |
            | Individual title           | Mr                           |
            | Individual forename        | Paul                         |
            | Individual surname         | Young                        |
            | Application Raised to date | 2014-07-17                   |
            | Application Raised date    | 2015-01-15                   |
            | National Insurance Number  | PY123456B                    |
            | Threshold                  | 357.69                       |

#New Scenario -
    Scenario: Raj Patel does not meet the Category A employment duration Requirement (He has worked for his current employer for only 3 months)

    He earns £600 a Week Gross Income BUT for only 12 weeks
    He worked for a different employer before his current employer

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | RP123456C  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mr                 |
            | Individual forename        | Raj                |
            | Individual surname         | Patel              |
            | Application Raised to date | 2015-01-02         |
            | Application Raised date    | 2015-07-03         |
            | National Insurance Number  | RP123456C          |
            | Threshold                  | 357.69             |


#New Scenario -
    Scenario: John James does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    He earns £357.70 a Week Gross Income BUT for 25 weeks
    He worked for a different employer before his current employer on week 26 and earned £357

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JJ123456A  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mr                 |
            | Individual forename        | John               |
            | Individual surname         | James              |
            | Application Raised to date | 2015-01-02         |
            | Application Raised date    | 2015-07-03         |
            | National Insurance Number  | JJ123456A          |
            | Threshold                  | 357.69             |

#New Scenario -
    Scenario: Peter Jones does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    He earns £658.50 a Week Gross Income BUT for 25 weeks
    He worked for a different employer before his current employer on week 26 and earned £357

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | PJ123456A  |
            | Application Raised Date | 2015-07-03 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mr                 |
            | Individual forename        | Peter              |
            | Individual surname         | Jones              |
            | Application Raised to date | 2015-01-02         |
            | Application Raised date    | 2015-07-03         |
            | National Insurance Number  | PJ123456A          |
            | Threshold                  | 357.69             |

#New Scenario -
    Scenario: Jenny Francis does not meet the Category A employment duration Requirement (He has worked for his current employer for 6 months)

    She earns £658.50 a Week Gross Income BUT for 23 weeks

        Given A service is consuming the Income Proving TM Family API
        When the Income Proving TM Family API is invoked with the following:
            | NINO                    | JF123456A  |
            | Application Raised Date | 2015-05-12 |

        Then The Income Proving TM Family API provides the following result:
            | HTTP Status                | 200                |
            | Financial requirement met  | false              |
            | Failure reason             | NOT_ENOUGH_RECORDS |
            | Individual title           | Mrs                |
            | Individual forename        | Jenny              |
            | Individual surname         | Francis            |
            | Application Raised to date | 2014-11-11         |
            | Application Raised date    | 2015-05-12         |
            | National Insurance Number  | JF123456A          |
            | Threshold                  | 357.69             |
