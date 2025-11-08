package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.transfer.response.UserActivityLogResponse;
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
}
