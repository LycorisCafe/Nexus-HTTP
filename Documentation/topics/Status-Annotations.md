# Status Annotations

In this section you will learn how to use status annotations.

> Status annotations are used (mostly) to set redirections. Let's say deprecated endpoint or temporary moved resource.
> Then the best way to avoid manual redirection is to use these status annotations.

> `@Found` - HTTP status code `302 Found` with `Location` header
> `@Gone` - HTTP status code `410 Gone`
> `@PermanentRedirect` - HTTP status code `308 Permanent Redirect` with `Location` header
> `@MovedPermanently` - HTTP status code `301 Moved Permanently` with `Location` header
> `@SeeOther` - HTTP status code `303 See Other` with `Location` header
> `@Temporaryedirect` - HTTP status code `307 Temporary Redirect` with `Location` header
> `@UnavailableForLegalReasons` - HTTP status code `451 Unavailable For Legal Reasons`  with `Link` header

In this example, the `/deprecatedEndpoint` will be permanently redirected to the home page `/`. 

```java
@PermanentRedirect("/")
@GET("/deprecatedEndpoint")
public static HttpResponse deprecatedEndpoint(HttpGetRequest request,
                                              HttpResponse response) {
    return response;
}
```

> You can always find full tutorial source code in [here!](%sample-codes%)