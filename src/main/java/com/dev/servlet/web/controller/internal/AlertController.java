package com.dev.servlet.web.controller.internal;

import com.dev.servlet.infrastructure.alert.AlertService;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import com.dev.servlet.web.controller.AlertControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
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
