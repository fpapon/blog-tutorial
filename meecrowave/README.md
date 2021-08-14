<!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The xxx licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->
# Kamino blog tutorial

## Meecrowave

Build the project:

```shell
mvn clean install
```

Run the project:

```shell
mvn meecrowave:run
```

### Build project with docker

Package the assembly:

```shell
mvn meecrowave:bundle
```

Build the docker image:

```shell
mvn compile jib:dockerBuild
```

You can see the image on your local docker registry:

```shell
docker images
>
REPOSITORY                                                  TAG                   IMAGE ID            CREATED             SIZE
fpaponapache.azurecr.io/fpaponapache/meecrowave-custom-distrib   1.0.0-SNAPSHOT        8ed6cc4bae93        2 days ago          228MB
```

Now you can run a container:
```shell
docker run -it --name meecrowave fpaponapache.azurecr.io/fpaponapache/meecrowave-custom-distrib:1.0.0-SNAPSHOT
```

### Calling the api from docker

Create a customer:

```shell
curl -X POST http://172.17.0.2:8080/tutorial/customer \
     -H 'Content-Type: application/json' \
     -d '{
            "firstname": "toto",
            "lastname": "titi",
            "nationalId": "12345654321"
         }'
```

Fetch all the customers:

```shell
curl http://172.17.0.2:8080/tutorial/customer \
    -H 'Accept: application/json'
```

