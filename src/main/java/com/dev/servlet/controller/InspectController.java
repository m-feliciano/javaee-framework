package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.service.internal.ControllerIntrospectionService;
import com.dev.servlet.service.internal.inspector.ControllerInfo;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

@Singleton
@NoArgsConstructor
@Controller("inspect")
public class InspectController extends BaseController {

    @Inject
    private ControllerIntrospectionService inspector;

    @RequestMapping(value = "/raw", method = GET, requestAuth = false)
    public IHttpResponse<String> rawJson() {
        List<ControllerInfo> controllers = inspector.listControllers();
        String json = CloneUtil.toJson(controllers);
        return HttpResponse.ok(json).next(forwardTo("inspect-raw")).build();
    }

    @RequestMapping(value = "/info", requestAuth = false)
    public IHttpResponse<List<ControllerInfo>> page() {
        List<ControllerInfo> controllers = inspector.listControllers();
        return newHttpResponse(200, controllers, forwardTo("inspect"));
    }
}
