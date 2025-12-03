package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.controller.AlertControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.out.alert.AlertPort;
import com.dev.servlet.application.transfer.Alert;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class AlertController extends BaseController implements AlertControllerApi {
    @Inject
    private AlertPort alertPort;

    public IHttpResponse<String> list(String auth) {
        String userId = authenticationPort.extractUserId(auth);
        List<Alert> alerts = alertPort.list(userId);
        String json = CloneUtil.toJson(alerts);
        return HttpResponse.ofJson(json);
    }

    public IHttpResponse<Void> clear(String auth) {
        String userId = authenticationPort.extractUserId(auth);
        alertPort.clear(userId);
        return HttpResponse.<Void>newBuilder().statusCode(204).build();
    }
}
