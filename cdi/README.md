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

## CDI with Apache OpenWebBean

We are using the CDI implementation from the ASF:

https://openwebbeans.apache.org/

### Build

Maven:

```shell
mvn clean install
```

### Run

Execute:

```shell
mvn exec:java
...
INFOS: OpenWebBeans Container has started, it took [92] ms.
```

## Native build with GraalVM and Apache Geronimo Arthur

We are using the Apache Geronimo Arthur project as a helper for GraalVM compilation:

https://geronimo.apache.org/arthur/

### Build

Maven:

```shell
mvn arthur:native-image
```

### Run

Execute:

```shell
./target/cdi.graal.bin
...
INFO: OpenWebBeans Container has started, it took [2] ms.
```

### Docker

Thanks to Arthur, you can easily build a Docker image from your binary by running:

```shell
mvn arthur:docker
```

You need to build your binary first or use the full command:

```shell
mvn arthur:native-image arthur:docker
```
