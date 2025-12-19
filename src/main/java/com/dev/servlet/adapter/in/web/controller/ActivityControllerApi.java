package com.dev.servlet.adapter.in.web.controller;

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
    @RequestMapping(
            value = "/history",
            description = "Retrieve paginated user activity logs."
    )
    IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, String auth);

    @RequestMapping(
            value = "/history/{id}",
            jsonType = ActivityRequest.class,
            description = "Retrieve detailed information about a specific user activity log."
    )
    IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, String auth);

    @RequestMapping(
            value = "/search",
            description = "Search user activity logs based on query parameters."
    )
    IHttpResponse<IPageable<UserActivityLogResponse>> search(Query query, IPageRequest pageRequest, String auth);

    @RequestMapping(
            value = "/timeline",
            description = "Retrieve a timeline of user activities."
    )
    IHttpResponse<List<UserActivityLogResponse>> getTimeline(Query query, String auth);
}
