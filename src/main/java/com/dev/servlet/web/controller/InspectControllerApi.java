package com.dev.servlet.web.controller;

import com.dev.servlet.infrastructure.web.vo.ControllerInfo;
import com.dev.servlet.web.annotation.Controller;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.response.IHttpResponse;

import java.util.List;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;

@Controller("inspect")
public interface InspectControllerApi {
    @RequestMapping(value = "/raw", method = GET, requestAuth = false)
    IHttpResponse<String> rawJson();

    @RequestMapping(value = "/info", requestAuth = false)
    IHttpResponse<List<ControllerInfo>> page();
}
