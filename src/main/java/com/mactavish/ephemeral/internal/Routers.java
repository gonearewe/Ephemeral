package com.mactavish.ephemeral.internal;

import com.mactavish.ephemeral.Bootstrap;
import com.mactavish.ephemeral.Request;
import com.mactavish.ephemeral.Response;
import com.mactavish.ephemeral.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Routers {
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    private TrieTree<MethodHandlerMap> routerTreeRoot ;

    public static Routers of(Set<Class<?>> routerClasses) {
        Map<List<String>, MethodHandlerMap> routerMap = new HashMap<>();
        // range all http handler methods in all related classes to finish routerMap
        for (Class<?> routerClass : routerClasses) {
            for (java.lang.reflect.Method handlerFunc : routerClass.getMethods()) {
                // base url from class annotation
                var routerPatterns = new LinkedList<>(Arrays.asList(routerClass.getAnnotation(Router.class).url().split("/")));

                // url from method annotation
                var requestAnnotation = handlerFunc.getAnnotation(RequestMapping.class);
                if (requestAnnotation == null) {
                    continue;
                }
                routerPatterns.addAll(Arrays.asList(requestAnnotation.url().split("/")));

                // record the whole url in the routerMap
                var url = routerPatterns.stream().filter(String::isBlank).collect(Collectors.toList());
                if (!routerMap.containsKey(url)) {
                    routerMap.put(url, new MethodHandlerMap());
                }
                for (var httpMethod : requestAnnotation.method()) {
                    routerMap.get(url).put(httpMethod, handlerFunc);
                }
            }
        }

        // construct a router trie-tree with routerMap and complete a Routers instance
        var routers = new Routers();
        routers.routerTreeRoot=TrieTree.of(routerMap);
        return routers;
    }

    public Response route(Request req){
        Path<MethodHandlerMap> path=this.routerTreeRoot.matchPath(Arrays.stream(req.url.split("/")).filter(s->!s.isBlank()).collect(Collectors.toList()));
        var handlerFunc=path.getValue().get(req.method);
        var params=handlerFunc.getParameters();

        var args=new Object[params.length];
        if(params.length<2){
            log.fatal("not enough params for request handler",new Exception("illegal request handler"));
            return null;
        }

        // add variable resolved from RequestMapping's regex url
        if(params.length>2) {
            for (int i = 2; i < params.length; i++) {
                var variable = params[i].getAnnotation(Param.class).value();
                args[i] = path.resolvevariable(variable);
            }
        }

        // fill in http request and an empty response
        args[0]=req;
        var response=new  Response();
        args[1]=response;

        try{
            handlerFunc.invoke(args);
        }catch (Exception e){
            log.fatal("invoke request handler",e);
        }

        return response;
    }

    private Routers() {
    }

    private static class MethodHandlerMap extends HashMap<com.mactavish.ephemeral.annotation.Method, Method> {
    }
}

class TrieTree<T> {
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    private final String pattern;
    private T value;
    private final Set<TrieTree<T>> children = new HashSet<>();

    static <T> TrieTree<T> of(Map<List<String>, T> treePaths) {
        var head = new TrieTree<T>("");
        for (var treePath : treePaths.entrySet()) {
            var cur = head;
            for (var iterator= treePath.getKey().iterator(); iterator.hasNext();) {
                var pattern=iterator.next();
                var optionalChild = cur.children.stream().filter(child -> child.pattern.equals(pattern)).findAny();
                if (optionalChild.isPresent()) { // follow the existed tree path
                    cur = optionalChild.get();
                }else { // create a new branch
                    var child=new TrieTree<T>(pattern);
                    cur.addChild(child);
                    cur=child;
                }
                // reach the path end
                if(!iterator.hasNext()){
                    cur.value=treePath.getValue();
                }
            }
        }
        return head;
    }

    private TrieTree(String pattern) {
        this.pattern = pattern;
    }

    // boolean match(String pattern) {
    //     return this.pattern.equals(pattern);
    // }

    void addChild(TrieTree<T> node) {
        children.add(node);
    }

    Path<T> matchPath(List<String> realPath) {
        // extract part for current tree node
        final String realPathPart = realPath.get(0);
        if (!Path.isVariable(realPathPart) && !Path.isWildcard(realPathPart)) {
            return null; // not a match
        }

        Path<T> path = null;
        // reach tree path end
        if (this.children.size() == 0 && realPath.size() == 1) { // a match
            path = new Path<>(this.value);
        } else if (this.children.size() == 0) { // extra path pattern
            return null;
        } else { // not yet reach tree path end
            // match children
            for (TrieTree<T> t : this.children) {
                var tmp = t.matchPath(realPath.subList(1, realPath.size()));
                if (tmp == null) {
                    continue;
                }
                if (path != null) {
                    log.error("more than one router path found: " + path.getPatterns() + " and " + tmp.getPatterns());
                } else {
                    path = tmp;
                }
            }
        }

        // match current trie-tree node
        if (path == null) {
            log.error("no router path found");
        } else {
            path.addPattern(this.pattern);
            if (Path.isVariable(realPathPart)) {
                path.defineVariable(Path.extractVariableName(this.pattern), realPathPart);
            }
        }

        return path;
    }
}

class Path<T> {
    private final List<String> patterns = new LinkedList<>();
    private final Map<String, String> variables = new HashMap<>();
    private final T value;

    static boolean isVariable(String pattern) {
        return pattern != null && pattern.startsWith("$");
    }

    static boolean isWildcard(String pattern) {
        return "*".equals(pattern);
    }

    static String extractVariableName(String name) {
        return name.substring(1);
    }

    Path(T value) {
        this.value = value;
    }

    void addPattern(String pattern) {
        this.patterns.add(0, pattern);
    }

    void defineVariable(String name, String value) {
        this.variables.put(name, value);
    }

    String resolvevariable(String name){
        return this.variables.get(name);
    }

    List<String> getPatterns() {
        return this.patterns;
    }

    T getValue(){
        return this.value;
    }
}
