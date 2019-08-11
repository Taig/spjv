# Snowplow JSON validator

> Implementation of [Snowplow engineering technical test instructions - JSON validation service](https://gist.github.com/goodits/20818f6ded767bca465a7c674187223e)

[![pipeline status](https://gitlab.com/taig-github/spjv/badges/master/pipeline.svg)](https://gitlab.com/taig-github/spjv/commits/master)
[![coverage report](https://gitlab.com/taig-github/spjv/badges/master/coverage.svg)](https://gitlab.com/taig-github/spjv/commits/master)

## Assumptions

- `GET /schema/id` returns a status message with a `getSchema` action, not just the schema itself
- Calling `POST /schema/id` twice with the same id is an error and does therefore not override the existing schema

## Implementation remarks

- Simple file system storage seems sufficient for this task and allows me to avoid the complexity of a database. The feature can however be added easily by providing a respective [SchemaStorage](/src/main/scala/io/taig/snowplow/SchemaStorage.scala) implementation.
- I picked release candidate / milestone dependencies as an opportunity to take a look  at new features such as the cats-effect `Blocker`. In a real-world production scenario I would of course choose stable releases.
- The usage of [json-schema-validator](https://github.com/java-json-tools/json-schema-validator) and [circe](https://github.com/circe/circe) is a bit of a mess causing me to go back and forth between JSON ASTs. Circe is however the JSON library I am most comfortable with, so I only fall back to json-schema-validator / jackson for schema specific use cases.

## Building an running

### sbt

✓ sbt  
✓ Java  
✗ Docker  

If `sbt` is available, running `sbt run` is sufficient to build and start the app. The server will become available on `http://localhost:8080/`.

For a more interactive feedback loop (primarily intended for development), [sbt-revolver](https://github.com/spray/sbt-revolver) is available via `sbt reStart`.

### docker

✗ sbt  
✗ Java  
✓ Docker  

Docker is primarily intended for CI purposes but can also be used to build the app when `sbt` is not available.

1. Build the image, named `spjv`
    ```
    docker build -t spjv . 
    ```

2. Start a container with the app mapped to port `8080`
    ```
    docker run -it \
      -v $PWD:/root/spjv/ \
      -p 8080:8080 \
      --entrypoint /bin/bash \
      spjv \
      sbt run /path/to/storage
    ```

3. Server will become available on `http://localhost:8080/`

## Running the executable

Instead of building the app yourself, you may as well just use the executable that is generated as part of the CI process with [sbt-assembly](https://github.com/sbt/sbt-assembly). The JAR file can be downloaded from every [CI pipeline](https://gitlab.com/taig-github/spjv/pipelines).

```
java -jar spjv-1.0.0.jar /path/to/storage
```

The storage path is optional. When omitted a temporary directory is used. Depending on your setup, that might not persist across app restarts though.

## Continuous Integration

Setting up CI for a project like this is probably out of scope and not expected. I do however believe that CI is an integral component of every software project and should never be an afterthought. This is why every project of mine relies on my [sbt-houserules](https://github.com/Taig/sbt-houserules) plugin, enabling GitLab CI setup in a matter of minutes.

The CI takes care of these tasks:

- Run test suite
- Generate code coverage report
- Check that the build triggers no warning messages
- Validate code format
- Publish code coverage HTML report
- Publish executable artifact

You can access the [CI pipelines](https://gitlab.com/taig-github/spjv/pipelines) on the GitLab mirror.