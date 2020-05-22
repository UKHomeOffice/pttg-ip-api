Income Proving API
=

[![Docker Repository on Quay](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api/status "Docker Repository on Quay")](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api)

## Overview

This is the Income Proving API. The service talks with HMRC via [pttg-ip-hmrc] to retrieve previous incomes and employments and calculates if a migrant has sufficient income to support themselves and any associated dependants during their time in the UK.  

Currently the only client of this service is [pttg-ip-fm-ui].

## Find Us

* [GitHub]

## Technical Notes

The API is implemented using Spring Boot and exposes a RESTFul interface.

The endpoint is defined in `FinancialStatusResource.java#getFinancialStatus`.

## Building

This service is built using Gradle on [Drone] using [Drone yaml]

### Infrastructure

This service is packaged as a Docker image and stored on [Quay.io]

This service is deployed by [Drone] onto a Kubernetes cluster running on the ACP platform using its [Kubernetes configuration]

## Running Locally

Check out the project and run the command `./gradlew bootRun` which will install gradle locally, download all dependencies, build the project and run it.

The API should then be available on http://localhost:8081/incomeproving/v3/individual/financialstatus, where:
- port 8081 is defined in `application.properties` with key `server.port`
- path `/incomeproving/v3/individual/financialstatus` is defined in `FinancialStatusResource.java#getFinancialStatus`
- the expected request body contains a JSON representation of `FinancialStatusRequest.java`

Note that this API needs collaborating services [pttg-ip-audit] and [pttg-ip-hmrc]. Connection details for these services can be found in `application.properties` with keys `hmrc.service.*` and `pttg.audit.*`, which should include the default ports of the services. 

## Dependencies

This service depends upon:
* [pttg-ip-hmrc]
* [pttg-ip-audit]

## Versioning

For the versions available, see the [tags on this repository].

## Authors

See also the list of [contributors] who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENCE.md]
file for details.

[contributors]:                     https://github.com/UKHomeOffice/pttg-ip-api/graphs/contributors
[pttg-ip-hmrc]:                     https://github.com/UKHomeOffice/pttg-ip-hmrc
[pttg-ip-fm-ui]:                    https://github.com/UKHomeOffice/pttg-ip-fm-ui
[Quay.io]:                          https://quay.io/repository/ukhomeofficedigital/pttg-ip-api
[kubernetes configuration]:         https://github.com/UKHomeOffice/kube-pttg-ip-api
[Drone yaml]:                       .drone.yml
[tags on this repository]:          https://github.com/UKHomeOffice/pttg-ip-api/tags
[LICENCE.md]:                       LICENCE.md
[GitHub]:                           https://github.com/orgs/UKHomeOffice/teams/pttg
[Drone]:                            https://drone.acp.homeoffice.gov.uk/UKHomeOffice/pttg-ip-api
[pttg-ip-hmrc]:                     https://github.com/UKHomeOffice/pttg-ip-hmrc
[pttg-ip-audit]:                    https://github.com/UKHomeOffice/pttg-ip-audit
