package com.mactavish.ephemeral;

import com.mactavish.ephemeral.annotation.Method;

public class Request {
    public final String url;
    public final Method method;
    Request(String url,Method method){
        this.url=url;
        this.method=method;
    }
}
