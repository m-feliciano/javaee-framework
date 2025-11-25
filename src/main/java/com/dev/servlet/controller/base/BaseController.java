package com.dev.servlet.controller.base;

import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.response.IServletResponse;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.records.KeyPair;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static com.dev.servlet.core.util.ClassUtil.findControllerOnInterfaceRecursive;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class BaseController extends BaseRouterController {

    protected static final String LIST = "list";

    @Setter(AccessLevel.PROTECTED)
    private String webService;

    @Inject
    public void setJwtUtils(JwtUtil jwts) {
        this.jwts = jwts;
    }

    protected BaseController() {
        this.webService = webServiceFromClass(this.getClass());
    }

    private static String webServiceFromClass(Class<?> clazz) {
        Controller controller = clazz.getAnnotation(Controller.class);
        if (controller != null) return controller.value();

        String fromInterfaces = findControllerOnInterfaceRecursive(clazz);
        if (fromInterfaces != null) return fromInterfaces;

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return webServiceFromClass(superClass);
        }

        throw new IllegalStateException("No @Controller annotation found on class or its interfaces: " + clazz.getName());
    }

    protected String redirectToCtx(String context) {
        return getNext("redirect:/api/v1/{webService}/{context}", context);
    }

    protected String redirectTo(String id) {
        return redirectToCtx(LIST).concat("/" + id);
    }

    protected String forwardTo(String page) {
        return getNext("forward:pages/{webService}/{context}.jsp", page);
    }

    private String getNext(String next, String context) {
        String replace = next.replace("{webService}", this.webService);
        replace = replace.replace("{context}", context);
        return replace;
    }

    protected IServletResponse newServletResponse(Set<KeyPair> response, String next) {
        return new IServletResponse() {
            @Override
            public int statusCode() {
                return 200;
            }
            @Override
            public Set<KeyPair> body() {
                return response;
            }
            @Override
            public String next() {
                return next;
            }
            @Override
            public boolean json() {
                return false;
            }
        };
    }

    protected <U> IHttpResponse<U> newHttpResponse(int status, U response, String nextPath) {
        return HttpResponse.<U>newBuilder().statusCode(status).body(response).next(nextPath).build();
    }

    protected <U> IHttpResponse<U> newHttpResponse(int status, String nextPath) {
        return HttpResponse.<U>newBuilder().statusCode(status).next(nextPath).build();
    }

    protected <U> IHttpResponse<U> okHttpResponse(U response, String nextPath) {
        return newHttpResponse(200, response, nextPath);
    }
}
