#!/usr/bin/env bash

DIR="$(dirname $0)"

cd "${DIR}/../api" || exit 1
gradle :api:bootRun
cd -

