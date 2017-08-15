Income Proving API
=

[![Build Status](https://drone.digital.homeoffice.gov.uk/api/badges/UKHomeOffice/pttg-ip-api/status.svg)](https://drone.digital.homeoffice.gov.uk/UKHomeOffice/pttg-ip-api)

[![Docker Repository on Quay](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api/status "Docker Repository on Quay")](https://quay.io/repository/ukhomeofficedigital/pttg-ip-api)

Overview
-

This is the Income Proving API.

Latest test reports and API documentation can be viewed at https://ukhomeoffice.github.io/pttg-ip-api/

Technical Notes
-

The API is implemented using Spring Boot.  It is intended to allow the team to compare and contrast with
the API previously built using Spark Java.



### Infrastructure

Apps are packaged as Docker images and stored on quay.io.

Apps are run on AWS managed by kubernetes.

### Philosophy

Everything runs in a Docker container.

Builds are portable, it should be possible to check the code out, build and run the tests without any constraints.

No dependence on Jenkins for plugins, etc.

## Building

### Everything in Docker

Following this approach Jobs execute a shell script that carries out the build within a Docker Container.  This
is implemented by the build.sh script which does the following:

	1. Create Docker image to execute build in
	2. Run docker image
	3. Build docker image to run jar from artifacts
	4. Push image to quay.io

#### Pros of this approach

* There will be no port conflicts as the only thing running in the container is the app.  During the
spike we found that Jenkins was running on port 8080 which is the port used by the app which meant the
 app had to be run a different port for testing.  This is different to how it will run in production.
* Jenkins can be easily replaced with another tool.  [Not sure why this would be neccessary given that
it is only used to poll git and then execute a shell script - essentially doing the job chron does but
using git changes instead to time event.]

#### Cons of this approach:

* Running the build inside a container adds overhead in terms of both build duration and scripting.
* Build scripts are more complex, have to cater for situation such as running a docker container
with a docker container.
* Running the build inside a container is slower than building directly on the Jenkins server as
all the dependent jar files have to be downloaded for each build as does Gradle.
* Cannot use Jenkins plugins to publish the Serenity BDD reports they would have to be extracted and
published. [The docker cp command does not support wildcards or recursive folder copying so it cannot
be used.]
* Jenkins cannot be used to view unit test coverage reports. They would need to be extracted and published.

### Build on Jenkins server using Gradle

The usual approach is to define a Job that executes the build steps on the Jenkins
server in a local workspace.  For this example a simple build step that executes various Gradle tasks
has been implemented.  The Gradle script uses a plugin that builds the docker image and a custom task
to push the image to quay.io.

#### Pros of this approach

* No need to create a short lived container to execute the build in.  This reduces the amount of
shell scripting.
* Gradle can be used for all build steps, same as for development as same as anyone who wanted to take
advantage of the project as open source.
* Builds are faster as Jenkins maintains a local cache for jar dependencies.
* Gradle only downloaded once, not for every build.
* The example Job that builds the project does not make use of any non-vanilla Jenkins features or
plugins so Jenkins could be swapped out.

#### Cons of this approach

* Port conflicts

### Other points

The 'Build on Jenkins server using Gradle' approach would allow some Jenkins plugins to be used to, for example, publish
the test reports.  Proving things uses BDD approach and the Serenity BDD tool to report the state of
the executable specification (cucumber feature files).  Publishing these reports would be more complex
using the 'Everything in Docker' approach.

Each image pushed to quay.io must have a unique tag.  This is achieved by appending the Jenkins build
number to the version number:

	quay.io/ukhomeofficedigital/uk.gov.digital.ho.proving.income.api:1.0.29

It is unlikely that every build on Jenkins should be pushed to quay.io.

Neither approach offers a method for easily automating a software release.  Solving this issue in
Gradle will most likely be easier.

## Deploy

### Overview

Containers are deployed and managed by Kubernetes running on AWS.
Images are pulled from quay.io.
Logging is via stdout.

### Kubernetes on AWS

Kubernetes is used for deploying, running and managing the Docker containers.  Simply put
Kuberentes is configured declaratively, you tell it what you want deploying (i.e. docker image), and
what you want running (i.e. number of docker containers).  Kubernetes then takes the appropriate
steps to achieve this.

A framework, kb8or (https://github.com/UKHomeOffice/kb8or), has been built to assist in this process.

The services folder contains all the files neccessary to carry out the deployment.  Following the
previously stated philosophy the deployment is executed from inside a Dcoker container.

The characteristics of the app are defined in the ReplicationController

	services/k8resoruces/pt-income-rc.yaml

This file declares the required number of running instances; how much cpu and memory each instance
gets; which port is exposed to the proxy; and any runtime configuration items that are to be passed
into the container as environment variables.

### Summary

In addition to run capacity and performance he only items that need to be considered from an
application delivery team perspective are:

* Provisioning the token used to authenticate to k8s
* Providing the correct version of the image to be deployed

All the other configuration should be boiler plate that only needs to be set up when a new application
development is started.

