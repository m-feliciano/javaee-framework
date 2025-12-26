package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.HealthController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.response.HealthStatus;
import com.dev.servlet.infrastructure.health.HealthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@DisplayName("HealthController Tests")
class HealthControllerTest extends BaseControllerTest {

    @Mock
    private HealthService healthService;

    @InjectMocks
    private HealthController healthController;

    @Override
    protected void setupAdditionalMocks() {
        healthController.setJwtUtils(authenticationPort);

        // Setup default health service mocks
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", System.currentTimeMillis());

        lenient().when(healthService.getHealthStatus()).thenReturn(healthStatus);
        lenient().when(healthService.getReadinessStatus()).thenReturn(healthStatus);
        lenient().when(healthService.getLivenessStatus()).thenReturn(healthStatus);
        lenient().when(healthService.isDatabaseHealthy()).thenReturn(true);
        lenient().when(healthService.isCacheHealthy()).thenReturn(true);
    }

    @Nested
    @DisplayName("Health Check Tests")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return health status")
        void shouldReturnHealthStatus() {
            IHttpResponse<Map<String, Object>> response = healthController.health();

            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).containsKey("status");
            verify(healthService).getHealthStatus();
        }
    }

    @Nested
    @DisplayName("Readiness Tests")
    class ReadinessTests {

        @Test
        @DisplayName("Should return readiness status")
        void shouldReturnReadinessStatus() {
            IHttpResponse<Map<String, Object>> response = healthController.readiness();

            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            verify(healthService).getReadinessStatus();
        }
    }

    @Nested
    @DisplayName("Liveness Tests")
    class LivenessTests {

        @Test
        @DisplayName("Should return liveness status")
        void shouldReturnLivenessStatus() {
            IHttpResponse<Map<String, Object>> response = healthController.liveness();

            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            verify(healthService).getLivenessStatus();
        }
    }

    @Nested
    @DisplayName("Up Check Tests")
    class UpCheckTests {

        @Test
        @DisplayName("Should return UP when healthy")
        void shouldReturnUp() {
            HttpResponse<HealthStatus> response = healthController.up();

            assertThat(response).isNotNull();
            assertThat(response.body().status()).contains("UP");
            verify(healthService).isDatabaseHealthy();
            verify(healthService).isCacheHealthy();
        }
    }

    @Nested
    @DisplayName("Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement HealthControllerApi interface")
        void shouldImplementInterface() {
            assertThat(healthController).isInstanceOf(HealthControllerApi.class);
        }
    }
}

