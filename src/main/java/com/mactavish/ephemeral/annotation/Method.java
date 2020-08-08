package com.mactavish.ephemeral.annotation;

import io.netty.handler.codec.http.HttpMethod;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public enum Method {
    GET, POST, PUT, DELETE;

    public static Method of(HttpMethod method) {
        if(method.equals(HttpMethod.GET)){
            return GET;
        }else if(method.equals(HttpMethod.POST)){
            return POST;
        }else if (method.equals(HttpMethod.PUT)){
            return PUT;
        }else if (method.equals(HttpMethod.DELETE)){
            return DELETE;
        }else {
            System.out.println("http method not supported "+method);
            return null;
        }
    }
}
