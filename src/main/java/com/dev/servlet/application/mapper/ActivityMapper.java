package com.dev.servlet.application.mapper;

import com.dev.servlet.application.transfer.response.UserActivityLogResponse;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ActivityMapper {
    UserActivityLogResponse toResponse(UserActivityLog activityLog);

    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "responsePayload", ignore = true)
    @Mapping(target = "requestPayload", ignore = true)
    UserActivityLogResponse toResponseDashBoard(UserActivityLog activityLog);

    default UserActivityLog toFilter(String userId, Query query) {
        UserActivityLog filter = UserActivityLog.builder().userId(userId).build();
        if (query.parameters().get("status") != null) {
            String status = query.parameters().get("status").toUpperCase();
            filter.setStatus(ActivityStatus.valueOf(status));
        }
        if (query.parameters().get("name") != null) {
            filter.setAction(query.parameters().get("name"));
        }

        return filter;
    }
}
