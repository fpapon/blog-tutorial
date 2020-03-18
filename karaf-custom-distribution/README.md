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

## Karaf custom distribution

### Build project with docker

```
mvn compile jib:dockerBuild
```

You can see the image on your local docker registry:

```
docker images
>
REPOSITORY                                                  TAG                   IMAGE ID            CREATED             SIZE
fpaponapache.azurecr.io/fpaponapache/karaf-custom-distrib   1.0.0-SNAPSHOT        8ed6cc4bae93        2 days ago          230MB
```

Now you can run a container:

```
docker run -it --name karaf-custom fpaponapache.azurecr.io/fpaponapache/karaf-custom-distrib:1.0.0-SNAPSHOT
```

### Setting up JMX access

Management Karaf configuration is set in the `src/main/karaf/assembly-property-edits.xml` thanks to the **karaf-maven-plugin**.
By default, the management port are only exposed to localhost, but in production or remote docker container you may want to expose on published interfaces.

This will set the value in the `etc/org.apache.karaf.management.cfg` config file of the custom distribution.

```
rmiRegistryHost=0.0.0.0
rmiServerHost=0.0.0.0
```

### Connecting with VisualVM on remote docker container

- Check your remote docker container host ip with `docker inspect` command.
- Add a new remote host.
- Add a new JMX connection.

*Configuration*:
- Connection: `service:jmx:rmi://172.17.0.2:1099/jndi/rmi://172.17.0.2:1099/karaf-root`
- Display-name: `karaf-docker`
- Use security credentials: `username=karaf` / `password=karaf`

NB: at the end of the url connection, the karaf instance name is **root** and you can see your instance name in the `$KARAF_HOME/instances/instance.properties`

```
/karaf/instances # cat instance.properties 
count = 1
item.0.name = root
item.0.loc = /karaf
item.0.pid = 61
item.0.root = true
```

