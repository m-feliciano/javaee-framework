package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.domain.service.UserActivityService;
import com.dev.servlet.infrastructure.persistence.dao.UserActivityLogDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Singleton
public class UserActivityServiceImpl extends BaseServiceImpl<UserActivityLog, String> implements UserActivityService {

    @Inject
    public UserActivityServiceImpl(UserActivityLogDAO logDAO) {
        super(logDAO);
    }

    public UserActivityLogDAO getActivityLogDAO() {
        return (UserActivityLogDAO) super.getBaseDAO();
    }

    @Override
    public void logActivity(UserActivityLog activityLog) {
        try {
            super.save(activityLog);
            log.debug("Activity logged successfully for user: {}", activityLog.getUserId());
        } catch (Exception e) {
            log.error("Failed to log activity for user: {}", activityLog.getUserId(), e);
        }
    }

    @Override
    public Optional<UserActivityLog> getActivityDetail(String activityId, String userId) {
        UserActivityLogDAO DAO = getActivityLogDAO();
        Optional<UserActivityLog> activityLog = DAO.findById(activityId);

        if (activityLog.isPresent() && !activityLog.get().getUserId().equals(userId)) {
            log.warn("User {} attempted to access activity log {} belonging to another user", userId, activityId);
            return Optional.empty();
        }
        return activityLog;
    }

    @Override
    public <U> List<U> getActivitiesByDateRange(String userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper) {
        UserActivityLogDAO dao = getActivityLogDAO();
        List<UserActivityLog> activities = dao.findByUserIdAndDateRange(userId, startDate, endDate);

        return activities.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }
}

