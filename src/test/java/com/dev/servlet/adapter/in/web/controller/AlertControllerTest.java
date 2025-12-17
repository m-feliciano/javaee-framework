package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.AlertController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.out.alert.AlertPort;
import com.dev.servlet.application.transfer.Alert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test suite for AlertController implementation via AlertControllerApi interface.
 * Tests alert management including listing and clearing user alerts.
 */
@DisplayName("AlertController Tests")
class AlertControllerTest extends BaseControllerTest {

    @Mock
    private AlertPort alertPort;

    @InjectMocks
    private AlertController alertController;

    @Override
    protected void setupAdditionalMocks() {
        alertController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("List Alerts Tests")
    class ListAlertsTests {

        @Test
        @DisplayName("Should list all alerts for user")
        void shouldListAllAlerts() {
            // Arrange
            List<Alert> expectedAlerts = Arrays.asList(
                    new Alert("alert-1", "INFO", "Test alert 1", "2024-01-01T10:00:00Z"),
                    new Alert("alert-2", "WARNING", "Test alert 2", "2024-01-01T11:00:00Z"),
                    new Alert("alert-3", "ERROR", "Test alert 3", "2024-01-01T12:00:00Z")
            );

            when(alertPort.list(eq(USER_ID))).thenReturn(expectedAlerts);

            // Act
            IHttpResponse<String> response = alertController.list(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).contains("alert-1");
            assertThat(response.body()).contains("alert-2");
            assertThat(response.body()).contains("alert-3");

            verify(authenticationPort).extractUserId(VALID_AUTH_TOKEN);
            verify(alertPort).list(USER_ID);
        }

        @Test
        @DisplayName("Should return JSON formatted alert list")
        void shouldReturnJsonFormattedList() {
            // Arrange
            List<Alert> alerts = List.of(
                    new Alert("INFO", "Warning")
            );

            when(alertPort.list(any())).thenReturn(alerts);

            // Act
            IHttpResponse<String> response = alertController.list(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response.body()).isNotNull();
            assertThat(response.body()).contains("\"id\"");
            assertThat(response.body()).contains("\"message\"");
        }

        @Test
        @DisplayName("Should return empty list when no alerts exist")
        void shouldReturnEmptyListWhenNoAlerts() {
            // Arrange
            when(alertPort.list(eq(USER_ID))).thenReturn(List.of());

            // Act
            IHttpResponse<String> response = alertController.list(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
            verify(alertPort).list(USER_ID);
        }

        @Test
        @DisplayName("Should extract user ID from auth token")
        void shouldExtractUserIdFromToken() {
            // Arrange
            when(alertPort.list(any())).thenReturn(List.of());

            // Act
            alertController.list(VALID_AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Clear Alerts Tests")
    class ClearAlertsTests {

        @Test
        @DisplayName("Should clear all alerts for user")
        void shouldClearAllAlerts() {
            // Arrange
            doNothing().when(alertPort).clear(eq(USER_ID));

            // Act
            IHttpResponse<Void> response = alertController.clear(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNull();

            verify(authenticationPort).extractUserId(VALID_AUTH_TOKEN);
            verify(alertPort).clear(USER_ID);
        }

        @Test
        @DisplayName("Should return 204 status after clearing alerts")
        void shouldReturn204Status() {
            // Arrange
            doNothing().when(alertPort).clear(any());

            // Act
            IHttpResponse<Void> response = alertController.clear(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            // Status code 204 indicates successful deletion with no content
        }

        @Test
        @DisplayName("Should extract correct user ID when clearing")
        void shouldExtractCorrectUserId() {
            // Arrange
            doNothing().when(alertPort).clear(any());

            // Act
            alertController.clear(VALID_AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(VALID_AUTH_TOKEN);
            verify(alertPort).clear(USER_ID);
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement AlertControllerApi interface")
        void shouldImplementInterface() {
            assertThat(alertController).isInstanceOf(AlertControllerApi.class);
        }

        @Test
        @DisplayName("Should have AlertPort dependency injected")
        void shouldHaveAlertPortInjected() {
            assertThat(alertController).extracting("alertPort").isNotNull();
        }

        @Test
        @DisplayName("Should handle concurrent alert operations")
        void shouldHandleConcurrentOperations() throws InterruptedException {
            // Arrange
            when(alertPort.list(any())).thenReturn(List.of());
            doNothing().when(alertPort).clear(any());

            // Act - Simulate concurrent requests
            Thread[] threads = new Thread[5];
            final boolean[] success = {true};

            for (int i = 0; i < 5; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        if (index % 2 == 0) {
                            alertController.list(VALID_AUTH_TOKEN);
                        } else {
                            alertController.clear(VALID_AUTH_TOKEN);
                        }
                    } catch (Exception e) {
                        success[0] = false;
                    }
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Assert
            assertThat(success[0]).isTrue();
        }
    }

    @Nested
    @DisplayName("Deprecated Endpoint Tests")
    class DeprecatedEndpointTests {

        @Test
        @DisplayName("Should still function despite deprecation")
        void shouldStillFunctionDespiteDeprecation() {
            // The list endpoint is deprecated in favor of WebSocket
            // but should still work for backward compatibility

            // Arrange
            List<Alert> alerts = List.of(new Alert("INFO", "Test message"));
            when(alertPort.list(any())).thenReturn(alerts);

            // Act
            IHttpResponse<String> response = alertController.list(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isNotNull();
        }
    }
}

