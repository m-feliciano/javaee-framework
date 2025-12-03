package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.controller.ActivityControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.mapper.ActivityMapper;
import com.dev.servlet.application.port.in.activity.GetActivityPageablePort;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodPort;
import com.dev.servlet.application.port.in.activity.GetUserActivityDetailPort;
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

import static com.dev.servlet.shared.util.DateUtil.YYYY_MM_DD;

@Slf4j
@ApplicationScoped
public class ActivityController extends BaseController implements ActivityControllerApi {
    @Inject
    private GetActivityPageablePort activityPageableUseCase;
    @Inject
    private GetUserActivityByPeriodPort activityByPeriodUseCase;
    @Inject
    private GetUserActivityDetailPort userActivityDetailUseCase;
    @Inject
    private ActivityMapper activityMapper;

    public IHttpResponse<IPageable<UserActivityLogResponse>> getHistory(PageRequest defaultPage, String auth) {
        final String userId = authenticationPort.extractUserId(auth);
        PageRequest pageRequest = PageRequest.of(
                defaultPage.getInitialPage(),
                defaultPage.getPageSize(),
                UserActivityLog.builder().userId(userId).build(),
                Sort.by("timestamp").descending()
        );
        var activityLogPage = activityPageableUseCase.getAllPageable(pageRequest, activityMapper::toResponse);
        return HttpResponse.ok(activityLogPage).next(forwardTo("history")).build();
    }

    public IHttpResponse<UserActivityLog> getActivityDetail(ActivityRequest request, String auth) {
        final String userId = authenticationPort.extractUserId(auth);
        Optional<UserActivityLog> optional = userActivityDetailUseCase.getActivityDetail(request.id(), userId);

        UserActivityLog activityLog = optional.orElseThrow(() -> new RuntimeException("Activity not found"));
        if (activityLog == null) {
            return HttpResponse.error(404, "Activity not found");
        }

        return HttpResponse.ok(activityLog).next(forwardTo("detail")).build();
    }

    public IHttpResponse<IPageable<UserActivityLogResponse>> search(Query query, IPageRequest pageRequest, String auth) {
        UserActivityLog filter = activityMapper.toFilter(authenticationPort.extractUserId(auth), query);
        pageRequest.setFilter(filter);
        var activities = activityPageableUseCase.getAllPageable(pageRequest, activityMapper::toResponseDashBoard);
        return HttpResponse.ok(activities).next(forwardTo("history")).build();
    }

    public IHttpResponse<List<UserActivityLogResponse>> getTimeline(Query query, String auth) {
        final String userId = authenticationPort.extractUserId(auth);
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
                userId, dateInitial, dateFinal, activityMapper::toResponse);
        return HttpResponse.ok(userActivities).next(forwardTo("timeline")).build();
    }
}
