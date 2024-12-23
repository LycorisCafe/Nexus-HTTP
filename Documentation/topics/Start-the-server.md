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

  // Start HTTP server on random port
  var httpServerConfiguration = new HttpServerConfiguration("io.github.lycoriscafe", "NexusTemp");
  var httpServer = new HttpServer(httpServerConfiguration);
  httpServer.initialize();
  ```

- To start an HTTP `secured` server, add the following lines.
  ```java
  // Start HTTPS server on random port
  var httpsServerConfiguration = new HttpsServerConfiguration("io.github.lycoriscafe", "NexusTemp",
        "myTrustStore", "password".toCharArray(), "myKeyStore", "password".toCharArray());
  var httpsServer = new HttpsServer(httpsServerConfiguration);
  httpsServer.initialize();
  ```

In a successful server initialization, you will be able to see a log in console like this.

```shell
2024-12-24 : 02:37:22 [main] tid=1 [INFO] org.reflections.Reflections - Reflections took 243 ms to scan 2 urls, producing 18 keys and 97 values
2024-12-24 : 02:37:22 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: _____ _____ __ __ _____ _____
2024-12-24 : 02:37:22 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: |   | |   __|  |  |  |  |   __|
2024-12-24 : 02:37:22 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: | | | |   __|-   -|  |  |__   |
2024-12-24 : 02:37:22 [main] tid=1 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: |_|___|_____|__|__|_____|_____| HTTP (API v1.0)
2024-12-24 : 02:37:22 [Thread-0] tid=30 [INFO] io.github.lycoriscafe.nexus.http.HttpServer - NEXUS-HTTP :: Server initialized @ 0.0.0.0/0.0.0.0:11653
```

When you navigate to the target IP (since it's `0.0.0.0` we can use `localhost` here) and port (`11653`) using browser (
`localhost:11653`), you will receive a message something like this with HTTP error code `404 Not Found`.

```json
{
  "errorMessage": "endpoint not found"
}
```

This is normal for now because we didn't write any endpoints yet.

