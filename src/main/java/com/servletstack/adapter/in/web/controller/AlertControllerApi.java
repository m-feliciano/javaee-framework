package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.IHttpResponse;

import static com.servletstack.domain.entity.enums.RequestMethod.POST;

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
    @RequestMapping(
            value = "/list",
            description = "Retrieve the list of alerts for the authorized user. Deprecated: Use the proper websocket connection to receive alerts."
    )
    IHttpResponse<String> list(String auth);

    @RequestMapping(
            value = "/clear",
            method = POST,
            description = "Clear all alerts for the authorized user."
    )
    IHttpResponse<Void> clear(String auth);
}
