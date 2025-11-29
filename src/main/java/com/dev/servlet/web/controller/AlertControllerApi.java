package com.dev.servlet.web.controller;

import com.dev.servlet.web.annotation.Authorization;
import com.dev.servlet.web.annotation.Controller;
import com.dev.servlet.web.annotation.RequestMapping;
import com.dev.servlet.web.response.IHttpResponse;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;
import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("alert")
public interface AlertControllerApi {
    @RequestMapping(value = "/list", method = GET)
    IHttpResponse<String> list(@Authorization String auth);

    @RequestMapping(value = "/clear", method = POST)
    IHttpResponse<Void> clear(@Authorization String auth);
}
