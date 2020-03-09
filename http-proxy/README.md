# Proxy

TCP proxy with Netty from example:

https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/proxy

## Usage

```
java -DlocalPort="8383" -DremoteHost="localhost" -DremotePort="61616" -jar target/http-proxy-1.0.0-SNAPSHOT.jar
```