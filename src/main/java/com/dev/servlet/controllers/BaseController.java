package com.dev.servlet.controllers;

import com.dev.servlet.controllers.router.BaseRouterController;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.IServletResponse;
import com.dev.servlet.interfaces.Identifier;
import com.dev.servlet.model.BaseModel;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.KeyPair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Base Controller for the application
 *
 * @param <T> the entity extends {@linkplain Identifier} of {@linkplain K}
 * @param <K> the entity id
 * @implNote You should extend this class and provide a Model specialization, which extends {@linkplain BaseModel}.
 * @see BaseModel
 */
@Slf4j
@NoArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class BaseController<T extends Identifier<K>, K> extends BaseRouterController {

    // Common paths
    private static final String FORWARD_TO = "forward:pages/{webService}/{context}.jsp"; // forward:pages/product/formCreateProduct.jsp
    private static final String REDIRECT_TO = "redirect:/api/v1/{webService}/{context}";
    protected static final String LIST = "list";

    @Getter
    protected BaseModel<T, K> baseModel;

    @Setter(AccessLevel.PROTECTED)
    private String webService;

    protected BaseController(BaseModel<T, K> baseModel) {
        this.baseModel = baseModel;
        this.webService = this.getClass().getAnnotation(Controller.class).path().substring(1);
    }

    /**
     * Redirect to the path
     *
     * @param context
     * @return - the next path
     */
    protected String redirectTo(String context) {
        return getNext(REDIRECT_TO, context);
    }

    /**
     * Redirect to list entity
     *
     * @param id - the entity id
     * @return - the next path
     */
    protected String redirectTo(K id) {
        return redirectTo("list").concat("/" + id);
    }

    /**
     * Forward to the path
     *
     * @param page
     * @return
     */
    protected String forwardTo(String page) {
        return getNext(FORWARD_TO, page);
    }

    /**
     * Get the next path
     *
     * @param webService
     * @param context
     * @return
     */
    private String getNext(String webService, String context) {
        String replace = webService.replace("{webService}", this.webService);
        replace = replace.replace("{context}", context);
        return replace;
    }

    /**
     * Build the {@linkplain IServletResponse} object
     *
     * @param response {@linkplain Set} of {@linkplain KeyPair} - the response data
     * @param next     the next path
     */
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
        };
    }

    /**
     * Build the {@linkplain IServletResponse} object
     *
     * @param response - the response data
     * @param nextPath the next path
     * @param <U>      the response type
     */
    protected <U> IHttpResponse<U> newHttpResponse(int status, U response, String nextPath) {
        return HttpResponse.<U>newBuilder().statusCode(status).body(response).next(nextPath).build();
    }

    /**
     * @see #newHttpResponse(int, Object, String)
     */
    protected <U> IHttpResponse<U> okHttpResponse(U response, String nextPath) {
        return newHttpResponse(200, response, nextPath);
    }
}
