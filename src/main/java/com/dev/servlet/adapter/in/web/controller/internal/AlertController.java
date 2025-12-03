package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.alert.AlertService;
import com.dev.servlet.adapter.in.web.controller.AlertControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@NoArgsConstructor
@Slf4j
@ApplicationScoped
public class AlertController extends BaseController implements AlertControllerApi {
    @Inject
    private AlertService alertService;

    public IHttpResponse<String> list(String auth) {
        String userId = authenticationPort.extractUserId(auth);
        List<AlertService.Alert> alerts = alertService.list(userId);
        String json = CloneUtil.toJson(alerts);
        return HttpResponse.ofJson(json);
    }

    public IHttpResponse<Void> clear(String auth) {
        String userId = authenticationPort.extractUserId(auth);
        alertService.clear(userId);
        return HttpResponse.<Void>newBuilder().statusCode(204).build();
    }
}
