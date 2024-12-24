# Add endpoints

In this section we will discuss how to add endpoint to our Nexus-HTTP server. To do that, we will create a new `Java`
class with name `Endpoints.java`.

```plain text
SampleCodes
|- src
|   |- main
|   |   |- java
|   |   |   |- io
|   |   |       |- github
|   |   |           |- lycoriscafe
|   |   |               |- tutorial
|   |   |                   |- Main.java
|   |   |                   |- Endpoints.java     <-
|   |   |- resources
|   |- test
|- pom.xml
```

## @HttpEndpoint

First of all, we need to mark the target class using the [
`@HttpEndpoint`](%javadoc%/io/github/lycoriscafe/nexus/http/core/HttpEndpoint.html) annotation. It will help to the
endpoints scanner to identify this class holding endpoints and trigger a scan.

```java

@HttpEndpoint
public class Endpoints {
}
```

## Request method annotations

To define a HTTP endpoint, we have [
`HTTP request method annotations`](%javadoc%/io/github/lycoriscafe/nexus/http/core/requestMethods/annotations/package-summary.html).

> `@GET` - GET request endpoint\
> `@POST` - POST request endpoint\
> `@PUT` - PUT request endpoint\
> `@DELETE` - DELETE request endpoint\
> `@HEAD` - HEAD request endpoint\
> `@OPTIONS` - OPTIONS request endpoint\
> `@PATCH` - PATCH request endpoint

There are few points that API users should follow.

- Annotated method must be `public` and `static`.
- Annotated method must use return type as [
  `HttpResponse`](%javadoc%/io/github/lycoriscafe/nexus/http/engine/ReqResManager/httpRes/HttpResponse.html).
- Annotated method must have 2 parameters. [
  `HttpRequest`](%javadoc%/io/github/lycoriscafe/nexus/http/engine/ReqResManager/httpReq/HttpRequest.html) type and
  [`HttpResponse`](%javadoc%/io/github/lycoriscafe/nexus/http/engine/ReqResManager/httpRes/HttpResponse.html)
  respectively.
- The class holding the annotated method must be annotated with [
  `HttpEndpoint`](%javadoc%/io/github/lycoriscafe/nexus/http/core/HttpEndpoint.html).

> When doing the in-method processing it's highly recommended to return the same HttpResponse that got as a method
> parameter with any changes (for more info, see HttpResponse class).

This is a sample `GET` endpoint. When you browse to this endpoint, you will receive a web page containing
`Sample Get endpoint!`.

```java

@GET("/sampleGetEndpoint")
public static HttpResponse sampleGetEndpoint(HttpGetRequest request,
                                             HttpResponse response) {
    // This will return HTTP status '200 OK' without any content
    return response.setContent(new Content("text/plain", "Sample Get endpoint!"));
}
```

> You can always find full tutorial source code in [here!](%sample-codes%)