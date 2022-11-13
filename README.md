# dt-documents-calculation-challenge
Design Technologies Document Calculation Challenge Implementation

## Requirements

* Docker (make sure that `--network host` option is working fine)
* Java 11+
* Gradle 7.3+

## How to

### Run the entire project as docker containers

```
bash scripts/docker-build-and-run.sh
```

### Run the API only

```
bash scripts/api-build-and-run.sh
```

### Generate `OpenAPI` server side code

```
docker run --rm \
  -v ${PWD}:/local openapitools/openapi-generator-cli generate \
  -i https://raw.githubusercontent.com/clippings/documents-calculation-challenge/master/openapi.yaml \
  -g spring \
  --additional-properties=basePackage=design.technologies.api,apiPackage=design.technologies.api.generated.api,modelPackage=design.technologies.api.generated.model,configPackage=design.technologies.api.generated.configuration,groupId=design.technologies.api,artifactId=design-technologies-api,useSpringController=true,title=DesignTechnologiesDocumentCalculation \
  -o /local/api/api
```


## API Backend

### General information

API backend folder: `api`

It is a multimodule `gradle` project with the following modules:

* `core` - the common interfaces, POJOs and utilities are there 
* `test` - the common test classes are there.  
  * IMPORTANT: include only as test dependency
* `csv` - module for parsing `*.csv` files
* `business` - this module is responsible for business related tasks
* `api` - the presentation layer. `OpenAPI` generated code is there and conversion between business DTOs and `OpenAPI` models is performed.

### OpenAPI UI
[Default API Backend endpoint](http://localhost:8080/) will lead to [Swagger UI](http://localhost:8080/swagger-ui.html)

### Run the API only

```
bash scripts/api-build-and-run.sh
```


### Test coverages 

`jacoco` plugin is used.

After executing `gradle build` or `gradle test` command the reports are generated as following:

[api module](api/api/build/reports/jacoco/test/html/index.html) : api/api/build/reports/jacoco/test/html/index.html

[business module](api/business/build/reports/jacoco/test/html/index.html) : api/business/build/reports/jacoco/test/html/index.html

[csv module](api/csv/build/reports/jacoco/test/html/index.html) : api/csv/build/reports/jacoco/test/html/index.html

## UI

UI is created (basic implementation) via [react-admin](https://marmelab.com/react-admin/)


After running 

```
bash scripts/docker-build-and-run.sh
```
it will be available on http://localhost:44445


Development also can be performed via executing this script:


```
bash scripts/docker-run-ui-work.sh
```

How to work with dev server is described in [UI README](ui/ui-admin/README.md)

