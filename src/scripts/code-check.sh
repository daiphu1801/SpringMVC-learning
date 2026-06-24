#!/usr/bin/env bash
set -euo pipefail

if [[ -x "./mvnw" ]]; then
    MVN="./mvnw"
else
    MVN="mvn"
fi

$MVN verify
