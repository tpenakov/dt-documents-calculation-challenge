#!/usr/bin/env bash

API_SERVICE_NAME=triphon-dt-documents-calculation-challenge-api
UI_WORK_SERVICE_NAME=triphon-dt-documents-calculation-challenge-ui-build
UI_SERVICE_NAME=triphon-dt-documents-calculation-challenge-ui
WORK_DIR=/project

DIR="$(dirname $0)"

docker stop ${UI_SERVICE_NAME} ${API_SERVICE_NAME}

buildUiCmd() {
	docker run --rm -it --network host \
		--name=${UI_WORK_SERVICE_NAME} \
		-v "$(pwd):${WORK_DIR}" \
		-v /tmp/$UI_WORK_SERVICE_NAME:/tmp \
		-w ${WORK_DIR} \
		node:current \
		"$@"
}


cd "${DIR}/../api" || exit 1
gradle :api:bootBuildImage
docker run --rm -it -d --network host \
	--name=${API_SERVICE_NAME} \
	triphon/dt-documents-calculation-challenge
cd -

cd "${DIR}/../ui/ui-admin" || exit 1
buildUiCmd yarn
buildUiCmd yarn build
cd -

cd "${DIR}/.." || exit 1
docker run -i --rm -d -p 44445:80 \
	--name=${UI_SERVICE_NAME} \
	--volume=ui/ui-admin/build:/var/www \
	--volume=scripts/nginx/conf.d:/etc/nginx/conf.d \
	nginx:1.16
cd -

echo "UI is available on http://localhost:44445"
echo "API is available on http://localhost:8080"

