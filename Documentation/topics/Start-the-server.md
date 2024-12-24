# Start the server

In this section, we talk about how to set up server configuration instance and start a simple HTTP server. In our code,
we can initialize more than one server at same time. As mentioned in the [`Nexus-HTTP`](index.md) page, you need to
satisfy the basic prerequisites.

## Server configuration

We have major 2 server configuration classes.

- [HttpServerConfiguration](%javadoc%/io/github/lycoriscafe/nexus/http/helper/configuration/HttpServerConfiguration.html) (
  for HTTP server)
- [HttpsServerConfiguration](%javadoc%/io/github/lycoriscafe/nexus/http/helper/configuration/HttpsServerConfiguration.html) (
  for HTTP `secured` server)

## Server initialization

We have major 2 server initialization classes.

- [HttpServer](%javadoc%/io/github/lycoriscafe/nexus/http/HttpServer.html) (for HTTP server)
- [HttpsServer](%javadoc%/io/github/lycoriscafe/nexus/http/HttpsServer.html) (for HTTP `secured` server)

## Apply

Let's apply them in to out code!

- As mentioned in the previous page, open the created `Main.java` file. In main method,
  add the following lines to start an HTTP server on random
  port. [[Source]](%sample-codes%/io/github/lycoriscafe/Main.java)
  ```java

  // Start HTTP server on port 80
  var httpServerConfiguration = new HttpServerConfiguration("io.github.lycoriscafe", "NexusTemp");
  var httpServer = new HttpServer(httpServerConfiguration);
  httpServer.initialize();
  ```

- To start an HTTP `secured` server, add the following lines.
  ```java
  // Start HTTPS server on port 443
  var httpsServerConfiguration = new HttpsServerConfiguration("io.github.lycoriscafe", "NexusTemp",
        "myTrustStore", "password".toCharArray(), "myKeyStore", "password".toCharArray());
  var httpsServer = new HttpsServer(httpsServerConfiguration);
  httpsServer.initialize();
  ```

In a successful server initialization, you will be able to see a log in console like this. The output may differ with
your `SLF4J` logger configurations.

```shell
2024-12-24 : 03:53:57 [main] tid=1 [INFO] org.reflections.Reflections - Reflections took 275 ms to scan 2 urls, producing 18 keys and 97 values
2024-12-24 : 03:53:57 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: _____ _____ __ __ _____ _____
2024-12-24 : 03:53:57 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: |   | |   __|  |  |  |  |   __|
2024-12-24 : 03:53:57 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: | | | |   __|-   -|  |  |__   |
2024-12-24 : 03:53:57 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: |_|___|_____|__|__|_____|_____| HTTP (API v1.0)
2024-12-24 : 03:53:57 [Nexus-HTTP@80] tid=30 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: Server initialized @ 0.0.0.0/0.0.0.0:80
```

When you navigate to the target IP (since it's `0.0.0.0` we can use `localhost` here) and port (`80`) using browser (
`localhost:80`), you will receive a message something like this with HTTP error code `404 Not Found`.

```json
{
  "errorMessage": "endpoint not found"
}
```

This is normal for now because we didn't write any endpoints yet.
> Take a look at the [
`HttpServerConfiguration`](%javadoc%//io/github/lycoriscafe/nexus/http/helper/configuration/HttpServerConfiguration.html)
> constructor. The first 2 parameters are `basePackage` and
`tempDirectory`.
> - `basePackage` - This is the point where the Nexus-HTTP will begin the endpoints scanning (we will discuss endpoints
    in next chapter). So **all classes inside the passed package** will be scanned.
> - `tempDirectory` - This is the place where the Nexus-HTTP will store **temporary files**. Like incoming requests,
    endpoints database, etc. The directory must have read and write permissions. It's highly recommended to use
    different directories if you start more than one server, to avoid conflicts.

> You can always find full tutorial source code in [here!](%sample-codes%)