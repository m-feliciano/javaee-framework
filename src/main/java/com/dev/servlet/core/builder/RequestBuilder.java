package com.dev.servlet.core.builder;

import com.dev.servlet.core.util.KeyPairJsonUtil;
import com.dev.servlet.core.util.URIUtils;
import com.dev.servlet.domain.service.internal.AuthCookieServiceImpl;
import com.dev.servlet.domain.transfer.Request;
import com.dev.servlet.domain.transfer.records.KeyPair;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import lombok.Builder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Builder(builderClassName = "RequestCreator", builderMethodName = "newBuilder")
public class RequestBuilder {
    private final HttpServletRequest httpServletRequest;

    public static class RequestCreator {
        private String endpoint;
        private String method;
        private String jsonBody;
        private String token;
        private IPageRequest pageRequest;
        private Query query;
        private int retry;

        public RequestCreator endpoint() {
            this.endpoint = httpServletRequest.getServletPath();
            return this;
        }

        public RequestCreator method() {
            this.method = httpServletRequest.getMethod();
            return this;
        }

        public RequestCreator body() {
            final String id = resolveId();
            List<KeyPair> parameters = URIUtils.getParameters(httpServletRequest);
            parameters.add(new KeyPair("id", id));
            this.jsonBody = KeyPairJsonUtil.toJson(parameters);
            return this;
        }

        public RequestCreator token() {
            this.token = token(httpServletRequest);
            return this;
        }

        public RequestCreator pageRequest() {
            this.pageRequest = URIUtils.getPageRequest(httpServletRequest);
            return this;
        }

        public RequestCreator query() {
            this.query = URIUtils.query(httpServletRequest);
            return this;
        }

        public RequestCreator retry(int retry) {
            this.retry = retry;
            return this;
        }

        public RequestCreator complete() {
            return this.endpoint().method().body().token().query().pageRequest();
        }

        public Request build() {
            return Request.builder()
                    .endpoint(endpoint).method(method).token(token).jsonBody(jsonBody)
                    .pageRequest(pageRequest).query(query).retry(retry)
                    .build();
        }

        private String token(HttpServletRequest request) {
            return AuthCookieServiceImpl.extractBearerToken(request);
        }

        private String resolveId() {
            String id = URIUtils.getResourceId(httpServletRequest);
            if (id != null) {
                endpoint = endpoint.substring(0, endpoint.lastIndexOf("/"));
                endpoint = endpoint.concat("/{id}");
            }
            return id;
        }
    }
}
