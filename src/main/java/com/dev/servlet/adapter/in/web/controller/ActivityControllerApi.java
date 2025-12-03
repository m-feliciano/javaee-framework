package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.ActivityRequest;
import com.dev.servlet.application.transfer.response.UserActivityLogResponse;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import com.dev.servlet.shared.vo.Query;

import java.util.List;

import static com.dev.servlet.domain.entity.enums.RequestMethod.GET;

@Controller("activity")
public interface ActivityControllerApi {
    @RequestMapping(value = "/history", method = GET)
    IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, @Authorization String auth);

    @RequestMapping(value = "/history/{id}", method = GET, jsonType = ActivityRequest.class)
    IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, @Authorization String auth);

    @RequestMapping(value = "/search", method = GET)
    IHttpResponse<IPageable<UserActivityLogResponse>> search(Query query, IPageRequest pageRequest, @Authorization String auth);

    @RequestMapping(value = "/timeline", method = GET)
    IHttpResponse<List<UserActivityLogResponse>> getTimeline(Query query, @Authorization String auth);
}
