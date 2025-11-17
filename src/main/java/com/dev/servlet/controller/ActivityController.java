package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.mapper.ActivityMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.DateUtil;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.model.enums.ActivityStatus;
import com.dev.servlet.service.UserActivityService;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.domain.records.Sort;
import com.dev.servlet.domain.request.ActivityRequest;
import com.dev.servlet.domain.response.UserActivityLogResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.infrastructure.persistence.internal.PageRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.dev.servlet.core.util.DateUtil.DD_MM_MYYYY;
import static com.dev.servlet.domain.model.enums.RequestMethod.GET;

@Slf4j
@NoArgsConstructor
@Controller("activity")
public class ActivityController extends BaseController {
    @Inject
    private UserActivityService activityService;
    @Inject
    private JwtUtil jwts;
    @Inject
    private ActivityMapper activityMapper;

    @RequestMapping(value = "/history", method = GET)
    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, @Authorization String auth) {
        final String userId = jwts.getUserId(auth);

        PageRequest pageRequest = PageRequest.of(
                defaultPage.getInitialPage(),
                defaultPage.getPageSize(),
                UserActivityLog.builder().userId(userId).build(),
                Sort.by("timestamp").descending()
        );

        var activityLogPage = activityService.getAllPageable(pageRequest, activityMapper::toResponse);
        return HttpResponse.ok(activityLogPage).next(forwardTo("history")).build();
    }

    @RequestMapping(value = "/history/{id}", method = GET, jsonType = ActivityRequest.class)
    public IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, @Authorization String auth) {
        final String userId = jwts.getUserId(auth);

        return activityService.getActivityDetail(request.id(), userId)
                .map(activity -> HttpResponse.ok(activity).next(forwardTo("detail")).build())
                .orElseGet(() -> HttpResponse.error(404, "Activity not found"));
    }

    @RequestMapping(value = "/search", method = GET)
    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistoryByAction(Query query,
                                                                                IPageRequest pageRequest,
                                                                                @Authorization String auth) {
        UserActivityLog filter = UserActivityLog.builder()
                .userId(jwts.getUserId(auth))
                .action(query.queries().get("name"))
                .build();

        if (query.queries().get("status") != null) {
            String status = query.queries().get("status").toUpperCase();
            filter.setStatus(ActivityStatus.valueOf(status));
        }

        pageRequest.setFilter(filter);

        var activities = activityService.getAllPageable(pageRequest, activityMapper::toResponseDashBoard);
        return HttpResponse.ok(activities).next(forwardTo("history")).build();
    }

    @RequestMapping(value = "/timeline", method = GET)
    public IHttpResponse<List<UserActivityLogResponse>> getTimeline(Query query, @Authorization String auth) {
        final String userId = jwts.getUserId(auth);

        String startDateStr = null;
        String endDateStr = null;
        if (query != null) {
            startDateStr = query.queries().get("startDate");
            endDateStr = query.queries().get("endDate");
        }

        String startDate = ObjectUtils.getIfNull(startDateStr, DateUtil.getTodayStartDateStringYYYYMMDD());
        Date dateInitial = DateUtil.toDateInitial(startDate, DD_MM_MYYYY);

        String endDate = ObjectUtils.getIfNull(endDateStr, DateUtil.getTodayEndDateStringYYYYMMDD());
        Date dateFinal = DateUtil.toDateFinal(endDate, DD_MM_MYYYY);

        var userActivities = activityService.getByPeriod(userId, dateInitial, dateFinal, activityMapper::toResponse);
        return HttpResponse.ok(userActivities).next(forwardTo("timeline")).build();
    }
}

