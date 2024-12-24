# Authenticated endpoints

In this section you will learn how to set up an authenticated endpoint. The basic idea is that, if you make
authenticated
endpoint and your request has no `Authorization` header, the API will automatically send a response with default
authentication challenges specified in [
`HttpServerConfiguration`](%javadoc%/io/github/lycoriscafe/nexus/http/helper/configuration/HttpServerConfiguration.html).

> Note: This API version (v%version%) only supports `Basic` and `Bearer` authentications only.

## `@Authenticated` annotation

This annotation is the basic authorization checking annotation. Let's say you set a `Basic` authentication challenge by
`httpServerConfiguration.addAuthentication()`, when you route to that endpoint, you will ask to provide a username and
password to access the endpoint.

> Note: Theis API does not process authentication other than the `Authorization` request header checking. So, API users
> need to implement their own authentication methods to verify the incoming requests.

Let's create another class to add authenticated methods.

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
|   |   |                   |- Endpoints.java
|   |   |                   |- AuthenticatedEndpoints.java     <-
|   |   |- resources
|   |- test
|- pom.xml
```

Any endpoint can be annotated with `@Authenticated`. The endpoint annotated with this,

- must `public` and `static`
- must annotate with any of request method annotation
- must return `HttpResponse` as return value
- must accept any type of `HttpRequest` as only parameter

In this example, to authenticate, we use `Basic` authentication scheme with username and password `root`.

> Note: If you're using the `@Authentication`, make sure to provide default authentication challenges to the
`HttpServerConfiguration`.

```java

@Authenticated
@GET("/sampleAuthenticatedEndpoint")
public static HttpResponse sampleAuthenticatedEndpoint(HttpGetRequest request,
                                                       HttpResponse response) {
    // Either these methods will work
    // if (request.getAuthorization() instanceof BasicAuthorization) {}
    if (request.getAuthorization().getAuthScheme() == AuthScheme.BASIC) {
        var authorization = (BasicAuthorization) request.getAuthorization();
        if (authorization.getUsername().equals("root") && authorization.getPassword().equals("root")) {
            return response.setContent(new Content("text/plain", "Authenticated!"));
        }
    }
    // ...
    return response.setContent(new Content("text/plain", "Unauthenticated!"));
}
```

> You can always find full tutorial source code in [here!](%sample-codes%)