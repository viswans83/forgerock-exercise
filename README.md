# ForgeRock Exercise

### Implemented Features
1. A spring-boot application containing a single endpoint `/test-transform` 
that meets the basic requirements of the problem.
2. Unit and Integration tests.

### Assumption Made
The example feature-configuration did not specify how the `event-id` field gets
produced. I took the liberty to introduce a `passThrough` setting that can specify
which fields are passed through without modification.

### Possible Improvements
1. Validate feature configurations, and return helpful error messages.
2. Persist feature-configs in a database. A workflow could be to first test 
a configuration using the `/test-transform` endpoint and then subsequently 
persist it when finalized. Then we could expose a new endpoint `/transform` 
that accepts the name of a feature-configuration (that was previously persisted) 
and an input json file. We would lookup the configuration from the database and 
apply the transformation.
3. Cache the jslt expressions instead of compiling them fresh for every request.
4. Logging, API documentation, and many more improvements.

### How to Run
1. Launch the application via `gradle bootRun`
2. Use `curl` to test the application. 
```bash
curl \
 -F "featureConfig=@feature-config.json;type=application/json" \
 -F "input=@sample-input.json;type=application/json" \
 localhost:8080/test-transform
```
The above assumes that files named `feature-config.json` and `sample-input.json` 
are present in the current working directory (e.g. from the `test/resources` folder)`. This should produce the output:
```bash
{"eventId":"878237843","device_os":"Linux","device_description":"Linux Laptop"}
```