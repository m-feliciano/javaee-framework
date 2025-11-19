package com.dev.servlet.service.internal.inspector;

import java.util.List;

public record MethodInfo(
        String path,
        String httpMethod,
        String jsonType,
        boolean requireAuth,
        List<String> roles,
        List<ParamInfo> params,
        String responseType
) {
}

