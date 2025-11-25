package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.AlertControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.service.internal.AlertService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@NoArgsConstructor
@Slf4j
@Singleton
public class AlertController extends BaseController implements AlertControllerApi {

    @Inject
    private AlertService alertService;

    public IHttpResponse<String> list(String auth) {
        String userId = jwts.getUserId(auth);
        List<AlertService.Alert> alerts = alertService.list(userId);
        String json = CloneUtil.toJson(alerts);
        return HttpResponse.ofJson(json);
    }

    public IHttpResponse<Void> clear(String auth) {
        String userId = jwts.getUserId(auth);
        alertService.clear(userId);
        return HttpResponse.<Void>newBuilder().statusCode(204).build();
    }
}

