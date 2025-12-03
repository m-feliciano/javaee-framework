package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;
import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("alert")
public interface AlertControllerApi {

    /**
     * Retrieve the list of alerts for the authorized user.
     *
     * @param auth Authorization token
     * @return List of alerts in String format
     * @deprecated Use the proper websocket connection to receive alerts
     */
    @Deprecated
    @RequestMapping(value = "/list", method = GET)
    IHttpResponse<String> list(@Authorization String auth);

    @RequestMapping(value = "/clear", method = POST)
    IHttpResponse<Void> clear(@Authorization String auth);
}
