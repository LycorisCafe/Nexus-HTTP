# Nexus-HTTP

**Nexus-HTTP** is a library that implements both embedded web sever and web framework just to make plug and play HTTP
server in Java environment. We aim to make this as lightweight, easy to use and feature rich as possible.

## Before you start

Before you begin, make sure you meet these prerequisites:

* Installed JDK `v21` or higher
* Apache Maven `v3.x` (or similar build system)

## Get started

To get started, create a new project. In here, we use the default `Maven` project structure.

```plain text
src
|- main
    |- java
        |- io
            |- github
                |- lycoriscafe
                    |- tutorial
                        |- Main.java
    |- resources
|- test
pom.xml
```

In `pom.xml` add the `Nexus-HTTP` dependency.
> You can find latest version of `Nexus-HTTP`
> from [here!](https://central.sonatype.com/artifact/io.github.lycoriscafe/nexus-http)

```xml

<dependency>
    <groupId>io.github.lycoriscafe</groupId>
    <artifactId>nexus-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

> You can always find full tutorial source code
> in [here!](https://github.com/lycoriscafe/nexus-http/src/test/java/README.md)