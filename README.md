### Dropwizard  with Gradle and Kotlin
------------
A "getting started" Dropwizard project in Kotlin with a Gradle build

To build and run:

```
./gradlew  build shadowJar && ./gradlew run
```
The app can also be run with:
 `java -jar build/libs/hello-dropwizard.jar server config.yml`


A test drive:
```
$ curl -X POST -H "Content-Type: application/json" \
  -d '{"id": 1, "content" : "bob"}' \
  http://localhost:8080/hello-world

{"id":1,"content":"bye bob"}

$ curl -X POST -H "Content-Type: application/json" \
  -d '{"id": 1, "content" : "bob123"}' \
  http://localhost:8080/hello-world

{"errors":["content length must be between 0 and 3"]}
```
