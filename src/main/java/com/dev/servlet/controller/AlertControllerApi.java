package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;
import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@Controller("alert")
public interface AlertControllerApi {

    @RequestMapping(value = "/list", method = GET)
    IHttpResponse<String> list(@Authorization String auth);

    @RequestMapping(value = "/clear", method = POST)
    IHttpResponse<Void> clear(@Authorization String auth);
}

