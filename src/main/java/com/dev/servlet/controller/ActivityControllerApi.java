package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.request.ActivityRequest;
import com.dev.servlet.domain.response.UserActivityLogResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.infrastructure.persistence.internal.PageRequest;

import java.util.List;

import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

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
