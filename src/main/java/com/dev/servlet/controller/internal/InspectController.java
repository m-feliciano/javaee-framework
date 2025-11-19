package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.InspectControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.service.internal.ControllerIntrospectionService;
import com.dev.servlet.service.internal.inspector.ControllerInfo;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
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
