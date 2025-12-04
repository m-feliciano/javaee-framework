package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.controller.InspectControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.introspection.ControllerIntrospectionService;
import com.dev.servlet.adapter.in.web.vo.ControllerInfo;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class InspectController extends BaseController implements InspectControllerApi {
    @Inject
    private ControllerIntrospectionService inspector;

    public IHttpResponse<String> rawJson() {
        List<ControllerInfo> controllers = inspector.listControllers();
        String json = CloneUtil.toJson(controllers);
        return HttpResponse.ok(json).next(forwardTo("inspect-raw")).build();
    }

    public IHttpResponse<List<ControllerInfo>> page() {
        List<ControllerInfo> controllers = inspector.listControllers();
        return newHttpResponse(200, controllers, forwardTo("inspect"));
    }
}
