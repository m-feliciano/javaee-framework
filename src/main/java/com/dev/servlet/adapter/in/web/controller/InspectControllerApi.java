package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.vo.ControllerInfo;

import java.util.List;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;

@Controller("inspect")
public interface InspectControllerApi {
    @RequestMapping(value = "/raw", method = GET, requestAuth = false)
    IHttpResponse<String> rawJson();

    @RequestMapping(value = "/info", requestAuth = false)
    IHttpResponse<List<ControllerInfo>> page();
}
