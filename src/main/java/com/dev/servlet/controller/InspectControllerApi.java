package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.service.internal.inspector.ControllerInfo;

import java.util.List;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

@Controller("inspect")
public interface InspectControllerApi {

    @RequestMapping(value = "/raw", method = GET, requestAuth = false)
    IHttpResponse<String> rawJson();

    @RequestMapping(value = "/info", requestAuth = false)
    IHttpResponse<List<ControllerInfo>> page();
}

