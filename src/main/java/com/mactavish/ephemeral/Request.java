package com.mactavish.ephemeral;

import com.mactavish.ephemeral.annotation.Method;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class Request {
    public final Method method;
    public final String url;
    public final HttpHeaders headers;

    // public final HttpContent content;
    public Request(HttpMethod method, String url, HttpHeaders headers) {
        this.method = Method.of(method);
        this.url = url;
        this.headers = headers;
    }
}
