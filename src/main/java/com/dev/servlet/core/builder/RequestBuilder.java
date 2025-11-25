package com.dev.servlet.core.builder;

import com.dev.servlet.core.util.KeyPairJsonUtil;
import com.dev.servlet.core.util.URIUtils;
import com.dev.servlet.domain.records.KeyPair;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.Request;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.dev.servlet.core.enums.ConstantUtils.ACCESS_TOKEN_COOKIE;
import static com.dev.servlet.core.enums.ConstantUtils.BEARER_PREFIX;

@Builder(builderClassName = "RequestCreator", builderMethodName = "newBuilder")
public record RequestBuilder(HttpServletRequest servletRequest) {

    public static class RequestCreator {
        private String endpoint;
        private String method;
        private String jsonBody;
        private String token;
        private IPageRequest pageRequest;
        private Query query;
        private int retry;

        public RequestCreator endpoint() {
            this.endpoint = servletRequest.getServletPath();
            return this;
        }

        public RequestCreator method() {
            this.method = servletRequest.getMethod();
            return this;
        }

        public RequestCreator body() {
            final String id = resolveId();
            List<KeyPair> parameters = URIUtils.getParameters(servletRequest);
            if (id != null && parameters.stream().noneMatch(p -> p.key().equals("id"))) {
                parameters.add(new KeyPair("id", id));
            }
            this.jsonBody = KeyPairJsonUtil.toJson(parameters);
            return this;
        }

        public RequestCreator token() {
            this.token = token(servletRequest);
            return this;
        }

        public RequestCreator pageRequest() {
            this.pageRequest = URIUtils.getPageRequest(servletRequest);
            return this;
        }

        public RequestCreator query() {
            this.query = URIUtils.query(servletRequest);
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
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                        return BEARER_PREFIX + cookie.getValue();
                    }
                }
            }
            return null;
        }

        private String resolveId() {
            String id = URIUtils.getResourceId(servletRequest);
            if (StringUtils.isBlank(id)) return null;

            endpoint = endpoint.substring(0, endpoint.lastIndexOf("/"));
            endpoint = endpoint.concat("/{id}");
            return id;
        }
    }
}
