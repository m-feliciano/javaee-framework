package com.servletstack.adapter.in.web.builder;

import com.servletstack.adapter.in.web.dto.Request;
import com.servletstack.domain.entity.enums.RequestMethod;
import com.servletstack.infrastructure.utils.KeyPairJsonUtil;
import com.servletstack.infrastructure.utils.URIUtils;
import com.servletstack.shared.vo.KeyPair;
import com.servletstack.shared.vo.Query;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.servletstack.shared.enums.ConstantUtils.ACCESS_TOKEN_COOKIE;
import static com.servletstack.shared.enums.ConstantUtils.BEARER_PREFIX;

@Builder(builderClassName = "RequestCreator", builderMethodName = "newBuilder")
public record RequestBuilder(HttpServletRequest servletRequest) {
    public static class RequestCreator {
        private String endpoint;
        private RequestMethod method;
        private String jsonBody;
        private String token;
        private Query query;
        private Integer retry;

        public RequestCreator endpoint() {
            this.endpoint = servletRequest.getRequestURI();
            return this;
        }

        public RequestCreator method() {
            this.method = RequestMethod.valueOf(servletRequest.getMethod());
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

        public RequestCreator query() {
            this.query = URIUtils.query(servletRequest);
            return this;
        }

        public RequestCreator retry(int retry) {
            this.retry = retry;
            return this;
        }

        public RequestCreator complete() {
            return this.endpoint().method().body().token().query();
        }

        public Request build() {
            return Request.builder()
                    .endpoint(endpoint).method(method).token(token).payload(jsonBody)
                    .query(query).retry(retry)
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
