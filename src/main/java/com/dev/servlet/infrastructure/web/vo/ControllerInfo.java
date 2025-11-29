package com.dev.servlet.infrastructure.web.vo;

import java.util.List;

public record ControllerInfo(String name, String basePath, List<MethodInfo> methods) {
}
