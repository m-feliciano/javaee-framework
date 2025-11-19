package com.dev.servlet.service.internal.inspector;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParamInfo(String name, String property) {
}

