Income Proving API
=

[![Build Status](https://drone.acp.homeoffice.gov.uk/api/badges/UKHomeOffice/pttg-ip-api/status.svg)](https://drone.acp.homeoffice.gov.uk/UKHomeOffice/pttg-ip-api)

[![Docker Repository on Quay](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api/status "Docker Repository on Quay")](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api)

Overview
-

This is the Income Proving API. Interfaces with the HMRC via [pttg-ip-hmrc] to retrieve previous incomes and employments and calculates if a migrant has sufficient income to support further family members migrating to the UK. 

Currently the only client of this service is [pttg-ip-fm-ui] though it is likely future version may be integrated directed with existing UKVI Case-working systems.

## Find Us

* [GitHub]
* [Quay.io]

### Technical Notes

The API is implemented using Spring Boot and exposes a RESTFul interface.

* /incomeproving/v2/individual/financialstatus
* /incomeproving/v2/individual/income

### Infrastructure

This service is packaged as a Docker image and stored on [Quay.io]

This service currently runs in AWS and has an associated [kubernetes configuration]

## Building

This service is built using Gradle on Drone using [Drone yaml]

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
[GitHub]:                           https://github.com/UKHomeOffice/pttg-ip-api
 
