#!/usr/bin/env bash

UI_WORK_SERVICE_NAME=triphon-dt-documents-calculation-challenge-ui-work
WORK_DIR=/project
DIR="$(dirname $0)"

cd "${DIR}/../ui" || exit 1

docker run --rm -it --network host \
	--name=${UI_WORK_SERVICE_NAME} \
	-v "$(pwd):${WORK_DIR}" \
	-v /tmp/$UI_WORK_SERVICE_NAME:/tmp \
	-w ${WORK_DIR} \
	node:current \
	$*

cd -
