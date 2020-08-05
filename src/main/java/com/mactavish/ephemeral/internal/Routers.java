package com.mactavish.ephemeral.internal;

import com.mactavish.ephemeral.Bootstrap;
import com.mactavish.ephemeral.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.*;

public class Routers {
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    private static final TrieTree routerTreeRoot = new TrieTree("");

    public Routers(Set<Class<?>> routerClasses) {
        Map<List<String>, Set<Method>> routerMap = new HashMap<>();
        for (Class<?> routerClass : routerClasses) {
            var routerPatterns = new LinkedList<>();
            routerPatterns.addAll(Arrays.asList(routerClass.getAnnotation(Router.class).url().split("/")));
            for (Method method : routerClass.getMethods()) {
                // this method doesn't have a HttpRequestAnnotation
                if (Arrays.stream(method.getAnnotations()).noneMatch(a->
                        com.mactavish.ephemeral.annotation.Method
                                .isHttpRequestAnnotation(a.getClass()))){
                    continue;
                }


            }
        }
    }

    private Class<? extends A>

}

class TrieTree {
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    private final String pattern;
    private final Set<TrieTree> children = new HashSet<>();

    TrieTree(String pattern) {
        this.pattern = pattern;
    }

    boolean match(String pattern) {
        return this.pattern.equals(pattern);
    }

    void addChild(TrieTree node) {
        children.add(node);
    }

    Path matchPath(List<String> realPath) {
        // reach tree path end
        if (realPath.size() == 0) {
            return new Path();
        }

        // extract part for current tree node
        final String realPathPart = realPath.get(0);
        if (!Path.isVariable(realPathPart) && !Path.isWildcard(realPathPart)) {
            return null; // not a match
        }

        // match children
        Path path = null;
        for (TrieTree t : this.children) {
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

class Path {
    private final List<String> patterns = new LinkedList<>();
    private final Map<String, Object> variables = new HashMap<>();

    static boolean isVariable(String pattern) {
        return pattern != null && pattern.startsWith("$");
    }

    static boolean isWildcard(String pattern) {
        return "*".equals(pattern);
    }

    static String extractVariableName(String name) {
        return name.substring(1);
    }

    void addPattern(String pattern) {
        this.patterns.add(0, pattern);
    }

    void defineVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    List<String> getPatterns() {
        return this.patterns;
    }
}
