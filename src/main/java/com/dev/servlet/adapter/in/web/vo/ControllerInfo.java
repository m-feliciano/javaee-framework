package com.dev.servlet.adapter.in.web.vo;

import java.util.List;

public record ControllerInfo(String name, String basePath, List<MethodInfo> methods) {
}
