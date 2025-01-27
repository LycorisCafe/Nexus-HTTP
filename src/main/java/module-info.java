module io.github.lycoriscafe.nexus.http {
    requires java.sql;
    requires org.reflections;
    requires org.slf4j;

    exports io.github.lycoriscafe.nexus.http;
    exports io.github.lycoriscafe.nexus.http.core;
    exports io.github.lycoriscafe.nexus.http.core.headers;
    exports io.github.lycoriscafe.nexus.http.core.headers.auth;
    exports io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.basic;
    exports io.github.lycoriscafe.nexus.http.core.headers.auth.scheme.bearer;
    exports io.github.lycoriscafe.nexus.http.core.headers.cache;
    exports io.github.lycoriscafe.nexus.http.core.headers.content;
    exports io.github.lycoriscafe.nexus.http.core.headers.cookies;
    exports io.github.lycoriscafe.nexus.http.core.headers.cors;
    exports io.github.lycoriscafe.nexus.http.core.headers.csp;
    exports io.github.lycoriscafe.nexus.http.core.headers.hsts;
    exports io.github.lycoriscafe.nexus.http.core.requestMethods;
    exports io.github.lycoriscafe.nexus.http.core.requestMethods.annotations;
    exports io.github.lycoriscafe.nexus.http.core.statusCodes;
    exports io.github.lycoriscafe.nexus.http.core.statusCodes.annotations;
    exports io.github.lycoriscafe.nexus.http.engine.reqResManager.httpReq;
    exports io.github.lycoriscafe.nexus.http.engine.reqResManager.httpRes;
    exports io.github.lycoriscafe.nexus.http.helper;
    exports io.github.lycoriscafe.nexus.http.helper.configuration;
    exports io.github.lycoriscafe.nexus.http.helper.models;
    exports io.github.lycoriscafe.nexus.http.helper.scanners;
    exports io.github.lycoriscafe.nexus.http.helper.util;
}