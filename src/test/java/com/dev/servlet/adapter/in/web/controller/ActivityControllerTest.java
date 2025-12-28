package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.ActivityController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.mapper.ActivityMapper;
import com.dev.servlet.application.port.in.activity.GetActivityPageablePort;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodPort;
import com.dev.servlet.application.port.in.activity.GetUserActivityDetailUseCase;
import com.dev.servlet.application.transfer.request.ActivityRequest;
import com.dev.servlet.application.transfer.response.UserActivityLogResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageResponse;
import com.dev.servlet.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ActivityController Tests")
class ActivityControllerTest extends BaseControllerTest {

    private static final UUID ACTIVITY_ID = UUID.randomUUID();
    @Mock
    private GetActivityPageablePort activityPageableUseCase;
    @Mock
    private GetUserActivityByPeriodPort activityByPeriodUseCase;
    @Mock
    private GetUserActivityDetailUseCase userActivityDetailUseCase;
    @Mock
    private ActivityMapper activityMapper;

    @InjectMocks
    private ActivityController activityController;

    @Override
    protected void setupAdditionalMocks() {
        activityController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("History Tests")
    class HistoryTests {

        @Test
        @DisplayName("Should retrieve paginated activity history")
        void shouldRetrievePaginatedHistory() {
            // Arrange
            PageRequest defaultPage = PageRequest.builder()
                    .initialPage(0)
                    .pageSize(10)
                    .build();

            IPageable<Object> expectedPage = mock(PageResponse.class);

            when(activityPageableUseCase.getAllPageable(any(IPageRequest.class), any()))
                    .thenReturn(expectedPage);

            // Act
            IHttpResponse<IPageable<UserActivityLogResponse>> response =
                    activityController.getHistory(defaultPage, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedPage);
            assertThat(response.next()).contains("forward:");
            assertThat(response.next()).contains("history");

            verify(authenticationPort).extractUserId(VALID_AUTH_TOKEN);
            verify(activityPageableUseCase).getAllPageable(any(IPageRequest.class), any());
        }
    }

    @Nested
    @DisplayName("Activity Detail Tests")
    class ActivityDetailTests {

        @Test
        @DisplayName("Should retrieve activity detail by ID")
        void shouldRetrieveActivityDetail() {
            // Arrange
            User user = User.builder().id(USER_ID).build();
            ActivityRequest request = new ActivityRequest(ACTIVITY_ID, "LOGIN", user);

            UserActivityLog activityLog = UserActivityLog.builder()
                    .id(ACTIVITY_ID)
                    .userId(USER_ID)
                    .action("LOGIN")
                    .build();

            when(userActivityDetailUseCase.getActivityDetail(eq(ACTIVITY_ID), eq(USER_ID)))
                    .thenReturn(Optional.of(activityLog));

            // Act
            IHttpResponse<UserActivityLog> response =
                    activityController.getActivityDetail(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(activityLog);

            verify(userActivityDetailUseCase).getActivityDetail(ACTIVITY_ID, USER_ID);
        }

        @Test
        @DisplayName("Should throw exception when activity not found")
        void shouldThrowExceptionWhenNotFound() {
            // Arrange
            User user = User.builder().id(USER_ID).build();
            ActivityRequest request = new ActivityRequest(UUID.randomUUID(), "LOGIN", user);

            when(userActivityDetailUseCase.getActivityDetail(any(), any()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> activityController.getActivityDetail(request, VALID_AUTH_TOKEN))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Activity not found");
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search activities with query parameters")
        void shouldSearchWithQueryParameters() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("action", "LOGIN");
            Query query = Query.builder().parameters(params).build();

            IPageRequest pageRequest = PageRequest.builder()
                    .initialPage(0)
                    .pageSize(10)
                    .build();

            UserActivityLog filter = UserActivityLog.builder().userId(USER_ID).build();

            when(activityMapper.toFilter(eq(USER_ID), eq(query))).thenReturn(filter);
            when(activityPageableUseCase.getAllPageable(any(), any()))
                    .thenReturn(mock(PageResponse.class));

            // Act
            IHttpResponse<IPageable<UserActivityLogResponse>> response =
                    activityController.search(query, pageRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();

            verify(activityMapper).toFilter(USER_ID, query);
            verify(activityPageableUseCase).getAllPageable(any(), any());
        }
    }

    @Nested
    @DisplayName("Timeline Tests")
    class TimelineTests {

        @Test
        @DisplayName("Should retrieve timeline with date range")
        void shouldRetrieveTimelineWithDateRange() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("startDate", "2024-01-01");
            params.put("endDate", "2024-12-31");
            Query query = Query.builder().parameters(params).build();

            List<Object> expectedActivities = new ArrayList<>();
            when(activityByPeriodUseCase.getByPeriod(eq(USER_ID), any(Date.class), any(Date.class), any()))
                    .thenReturn(expectedActivities);

            // Act
            IHttpResponse<List<UserActivityLogResponse>> response =
                    activityController.getTimeline(query, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedActivities);

            verify(activityByPeriodUseCase).getByPeriod(eq(USER_ID), any(Date.class), any(Date.class), any());
        }

        @Test
        @DisplayName("Should use default dates when query is null")
        void shouldUseDefaultDatesWhenQueryNull() {
            // Arrange

            List<Object> expectedActivities = new ArrayList<>();

            when(activityByPeriodUseCase.getByPeriod(eq(USER_ID), any(Date.class), any(Date.class), any()))
                    .thenReturn(expectedActivities);

            // Act
            IHttpResponse<List<UserActivityLogResponse>> response =
                    activityController.getTimeline(null, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();

            verify(activityByPeriodUseCase).getByPeriod(eq(USER_ID), any(Date.class), any(Date.class), any());
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement ActivityControllerApi interface")
        void shouldImplementInterface() {
            assertThat(activityController).isInstanceOf(ActivityControllerApi.class);
        }
    }
}
