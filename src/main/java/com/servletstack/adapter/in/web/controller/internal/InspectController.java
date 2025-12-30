package com.servletstack.adapter.in.web.controller.internal;

import com.servletstack.adapter.in.web.annotation.Cache;
import com.servletstack.adapter.in.web.controller.InspectControllerApi;
import com.servletstack.adapter.in.web.controller.internal.base.BaseController;
import com.servletstack.adapter.in.web.dto.HttpResponse;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.introspection.ControllerIntrospectionService;
import com.servletstack.adapter.in.web.vo.ControllerInfo;
import com.servletstack.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class InspectController extends BaseController implements InspectControllerApi {
    @Inject
    private ControllerIntrospectionService service;

    @Override
    protected Class<InspectController> implementation() {
        return InspectController.class;
    }

    @Cache(value = "inspect_raw_json")
    public IHttpResponse<String> rawJson() {
        List<ControllerInfo> controllers = service.listControllers();
        String json = CloneUtil.toJson(controllers);
        return HttpResponse.ok(json).next(forwardTo("inspect-raw")).build();
    }

    @Cache(value = "inspect_info")
    public IHttpResponse<List<ControllerInfo>> page() {
        List<ControllerInfo> controllers = service.listControllers();
        return newHttpResponse(200, controllers, forwardTo("inspect"));
    }
}
