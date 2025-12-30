package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.transfer.request.ActivityRequest;
import com.servletstack.application.transfer.response.UserActivityLogResponse;
import com.servletstack.domain.entity.UserActivityLog;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import com.servletstack.infrastructure.persistence.transfer.internal.PageRequest;
import com.servletstack.shared.vo.Query;

import java.util.List;

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
