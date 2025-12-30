package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.vo.ControllerInfo;

import java.util.List;

import static com.servletstack.domain.entity.enums.RequestMethod.GET;

@Controller("inspect")
public interface InspectControllerApi {
    @RequestMapping(
            value = "/raw",
            method = GET,
            requestAuth = false,
            description = "Retrieve raw JSON representation of all controllers and their endpoints."
    )
    IHttpResponse<String> rawJson();

    @RequestMapping(
            value = "/info",
            requestAuth = false,
            description = "Retrieve detailed information about all controllers and their endpoints."
    )
    IHttpResponse<List<ControllerInfo>> page();
}
