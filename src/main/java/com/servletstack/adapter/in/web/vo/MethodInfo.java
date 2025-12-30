package com.servletstack.adapter.in.web.vo;

import java.util.List;

public record MethodInfo(
        String path,
        String httpMethod,
        String jsonType,
        boolean requireAuth,
        List<String> roles,
        List<ParamInfo> params,
        String responseType,
        String description,
        boolean deprecated,
        boolean async
) {
}
