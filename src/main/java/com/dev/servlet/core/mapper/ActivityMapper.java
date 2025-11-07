package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.transfer.response.UserActivityLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ActivityMapper {
    UserActivityLogResponse toResponse(UserActivityLog activityLog);
}
