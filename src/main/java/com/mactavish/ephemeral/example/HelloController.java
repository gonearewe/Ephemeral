package com.mactavish.ephemeral.example;

import com.mactavish.ephemeral.Request;
import com.mactavish.ephemeral.Response;
import com.mactavish.ephemeral.annotation.Method;
import com.mactavish.ephemeral.annotation.Param;
import com.mactavish.ephemeral.annotation.RequestMapping;
import com.mactavish.ephemeral.annotation.Router;

@Router
public class HelloController {
    @RequestMapping
    public void root(Request req, Response res){
        res.html("hello world");
    }

    @RequestMapping(url = "/index")
    public void index(Request req,Response res){
        res.json(req.headers.entries());
    }

    @RequestMapping(url = "/person/*/name",method = Method.GET)
    public void person(Request req,Response res){
        res.html("visiting "+req.url);
    }

    @RequestMapping(url = "/library/$bookName/number",method = Method.PUT)
    public void library(Request req, Response res, @Param("bookName" )String name){
        res.html(""+name+"'s number is 20");
    }
}
