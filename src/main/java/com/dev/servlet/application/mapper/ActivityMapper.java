package com.dev.servlet.application.mapper;

import com.dev.servlet.application.transfer.response.UserActivityLogResponse;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.vo.Query;
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

        String status = query.get("status");
        if (status != null) {
            filter.setStatus(ActivityStatus.valueOf(status.toUpperCase()));
        }

        String name = query.get("name");
        if (name != null) {
            filter.setAction(name);
        }

        return filter;
    }
}
