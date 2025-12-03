package com.dev.servlet.adapter.in.web.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParamInfo(String name, String property) {
}
