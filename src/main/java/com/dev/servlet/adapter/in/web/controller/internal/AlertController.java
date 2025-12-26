package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
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
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class AlertController extends BaseController implements AlertControllerApi {
    @Inject
    private AlertPort alertPort;

    @Override
    protected Class<AlertController> implementation() {
        return AlertController.class;
    }

    public IHttpResponse<String> list(@Authorization String auth) {
        UUID userId = authenticationPort.extractUserId(auth);
        List<Alert> alerts = alertPort.list(userId);
        String json = CloneUtil.toJson(alerts);
        return HttpResponse.ok(json).build();
    }

    public IHttpResponse<Void> clear(@Authorization String auth) {
        UUID userId = authenticationPort.extractUserId(auth);
        alertPort.clear(userId);
        return HttpResponse.<Void>newBuilder().statusCode(204).build();
    }
}
