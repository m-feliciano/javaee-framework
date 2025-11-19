package com.dev.servlet.service.internal.inspector;

import java.util.List;

public record ControllerInfo(String name, String basePath, List<MethodInfo> methods) {
}