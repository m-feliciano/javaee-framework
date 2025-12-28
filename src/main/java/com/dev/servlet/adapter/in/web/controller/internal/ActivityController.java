package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.controller.ActivityControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.mapper.ActivityMapper;
import com.dev.servlet.application.port.in.activity.GetActivityPageableUseCase;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodUseCase;
import com.dev.servlet.application.port.in.activity.GetUserActivityDetailUseCase;
import com.dev.servlet.application.transfer.request.ActivityRequest;
import com.dev.servlet.application.transfer.response.UserActivityLogResponse;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import com.dev.servlet.shared.util.DateUtil;
import com.dev.servlet.shared.vo.Query;
import com.dev.servlet.shared.vo.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dev.servlet.shared.util.DateUtil.YYYY_MM_DD;

@Slf4j
@ApplicationScoped
public class ActivityController extends BaseController implements ActivityControllerApi {
    @Inject
    private GetActivityPageableUseCase activityPageableUseCase;
    @Inject
    private GetUserActivityByPeriodUseCase activityByPeriodUseCase;
    @Inject
    private GetUserActivityDetailUseCase userActivityDetailUseCase;
    @Inject
    private ActivityMapper mapper;

    @Override
    protected Class<ActivityController> implementation() {
        return ActivityController.class;
    }

    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, @Authorization String auth) {
        UUID userId = this.auth.extractUserId(auth);
        PageRequest pageRequest = PageRequest.of(
                defaultPage.getInitialPage(),
                defaultPage.getPageSize(),
                UserActivityLog.builder().userId(userId).build(),
                Sort.by("timestamp").descending()
        );
        var activityLogPage = activityPageableUseCase.getAllPageable(pageRequest, mapper::toResponse);
        return HttpResponse.ok(activityLogPage).next(forwardTo("history")).build();
    }

    public IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, @Authorization String auth) {
        UUID userId = this.auth.extractUserId(auth);
        Optional<UserActivityLog> optional = userActivityDetailUseCase.getActivityDetail(request.id(), userId);

        UserActivityLog activityLog = optional.orElseThrow(() -> new RuntimeException("Activity not found"));
        if (activityLog == null) {
            return HttpResponse.error(404, "Activity not found");
        }

        return HttpResponse.ok(activityLog).next(forwardTo("detail")).build();
    }

    public IHttpResponse<IPageable<UserActivityLogResponse>> search(Query query, IPageRequest pageRequest, @Authorization String auth) {
        UserActivityLog filter = mapper.toFilter(this.auth.extractUserId(auth), query);
        pageRequest.setFilter(filter);
        var activities = activityPageableUseCase.getAllPageable(pageRequest, mapper::toResponseDashBoard);
        return HttpResponse.ok(activities).next(forwardTo("history")).build();
    }

    public IHttpResponse<List<UserActivityLogResponse>> getTimeline(Query query, @Authorization String auth) {
        UUID userId = this.auth.extractUserId(auth);
        String startDateStr = null;
        String endDateStr = null;
        if (query != null) {
            startDateStr = query.get("startDate");
            endDateStr = query.get("endDate");
        }

        String startDate = ObjectUtils.getIfNull(startDateStr, DateUtil.getTodayStartDateStringYYYYMMDD());
        Date dateInitial = DateUtil.toDateInitial(startDate, YYYY_MM_DD);
        String endDate = ObjectUtils.getIfNull(endDateStr, DateUtil.getTodayEndDateStringYYYYMMDD());
        Date dateFinal = DateUtil.toDateFinal(endDate, YYYY_MM_DD);

        var userActivities = activityByPeriodUseCase.getByPeriod(
                userId, dateInitial, dateFinal, mapper::toResponse);
        return HttpResponse.ok(userActivities).next(forwardTo("timeline")).build();
    }
}
