package com.servletstack.application.mapper;

import com.servletstack.application.transfer.response.UserActivityLogResponse;
import com.servletstack.domain.entity.UserActivityLog;
import com.servletstack.domain.entity.enums.ActivityStatus;
import com.servletstack.shared.vo.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ActivityMapper {
    UserActivityLogResponse toResponse(UserActivityLog activityLog);

    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "responsePayload", ignore = true)
    @Mapping(target = "requestPayload", ignore = true)
    UserActivityLogResponse toResponseDashBoard(UserActivityLog activityLog);

    default UserActivityLog toFilter(UUID userId, Query query) {
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
