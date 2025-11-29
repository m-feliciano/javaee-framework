package com.dev.servlet.web.controller.internal;

import com.dev.servlet.infrastructure.utils.CloneUtil;
import com.dev.servlet.infrastructure.web.vo.ControllerInfo;
import com.dev.servlet.web.controller.InspectControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.introspection.ControllerIntrospectionService;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.util.List;

@ApplicationScoped
@NoArgsConstructor
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
