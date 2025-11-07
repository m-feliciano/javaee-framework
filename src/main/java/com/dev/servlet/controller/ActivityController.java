package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.mapper.ActivityMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.service.UserActivityService;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.records.Sort;
import com.dev.servlet.domain.transfer.request.ActivityRequest;
import com.dev.servlet.domain.transfer.response.UserActivityLogResponse;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.infrastructure.persistence.internal.PageRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;

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
    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, @Authentication String auth) {
        try {
            String userId = jwts.getUserId(auth);

            PageRequest pageRequest = PageRequest.of(
                    defaultPage.getInitialPage(),
                    defaultPage.getPageSize(),
                    UserActivityLog.builder().userId(userId).build(),
                    Sort.by("timestamp").descending()
            );

            IPageable<UserActivityLogResponse> history = activityService.getAllPageable(pageRequest, activityMapper::toResponse);
            return HttpResponse.ok(history).next(forwardTo("history")).build();

        } catch (Exception e) {
            log.error("Error fetching user history", e);
            return HttpResponse.error(500, "Internal server error");
        }
    }

    @RequestMapping(value = "/history/{id}", method = GET, jsonType = ActivityRequest.class)
    public IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, @Authentication String auth) {
        try {
            String userId = jwts.getUserId(auth);
            Optional<UserActivityLog> activity = activityService.getActivityDetail(request.id(), userId);
            if (activity.isEmpty()) {
                return HttpResponse.error(404, "Activity not found");
            }
            return HttpResponse.ok(activity.get()).next(forwardTo("detail")).build();

        } catch (Exception e) {
            log.error("Error fetching activity detail", e);
            return HttpResponse.error(500, "Internal server error");
        }
    }

    @RequestMapping(value = "/search", method = GET)
    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistoryByAction(Query query,
                                                                                IPageRequest pageRequest,
                                                                                @Authentication String auth) {
        try {
            User user = jwts.getUser(auth);
            UserActivityLog filter = UserActivityLog.builder()
                    .userId(user.getId())
                    .action(query.queries().get("name"))
                    .build();
            pageRequest.setFilter(filter);

            IPageable<UserActivityLogResponse> history = activityService.getAllPageable(pageRequest, activityMapper::toResponse);
            return HttpResponse.ok(history).next(forwardTo("history")).build();

        } catch (Exception e) {
            log.error("Error fetching user history by action", e);
            return HttpResponse.error(500, "Internal server error");
        }
    }
}

