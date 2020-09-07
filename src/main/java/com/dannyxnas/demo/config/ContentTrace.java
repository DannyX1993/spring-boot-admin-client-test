package com.dannyxnas.demo.config;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.security.core.Authentication;

public class ContentTrace {
    protected HttpTrace httpTrace;
    protected String requestBody;
    protected String responseBody;
    protected Authentication principal;
    public ContentTrace() {
    }
    public void setHttpTrace(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }

    public HttpTrace getHttpTrace() {
        return httpTrace;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Authentication getPrincipal() {
        return principal;
    }

    public void setPrincipal(Authentication principal) {
        this.principal = principal;
    }
}