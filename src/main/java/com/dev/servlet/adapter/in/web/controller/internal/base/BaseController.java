package com.dev.servlet.adapter.in.web.controller.internal.base;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.IServletResponse;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.shared.vo.KeyPair;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static com.dev.servlet.shared.util.ClassUtil.findControllerOnInterfaceRecursive;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class BaseController extends BaseRouterController {

    protected static final String LIST = "list";
    @Setter(AccessLevel.PROTECTED)
    private String webService;

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

    @Inject
    public void setJwtUtils(AuthenticationPort jwts) {
        this.authenticationPort = jwts;
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
            public Set<KeyPair> body() {
                return response;
            }

            @Override
            public String next() {
                return next;
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
