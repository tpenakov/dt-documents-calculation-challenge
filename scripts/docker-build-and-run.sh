#!/usr/bin/env bash

API_SERVICE_NAME=triphon-dt-documents-calculation-challenge-api
DIR="$(dirname $0)"

cd "${DIR}/../api" || exit 1
gradle :api:bootBuildImage
docker run --rm -it -d --network host \
	--name=${API_SERVICE_NAME} \
	triphon/dt-documents-calculation-challenge
cd -

