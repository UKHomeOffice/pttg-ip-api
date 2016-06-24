#!/usr/bin/env bash
set -e
[ -n "${DEBUG}" ] && set -x

GIT_COMMIT=${GIT_COMMIT:-$(git rev-parse --short HEAD)}
GIT_COMMIT=${GIT_COMMIT:0:7}

build() {
  ./gradlew "${@}"
}

build "build"
