package com.servletstack.adapter.out.cache;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CacheAdapter Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Usado para @BeforeAll e @AfterAll não estáticos
class CacheAdapterTest {

    private static final String TEST_NAMESPACE = "testNamespace";
    private static final UUID TEST_KEY = UUID.randomUUID();
    private static final String TEST_VALUE = "testValue";
    private CacheAdapter cacheAdapter;

    @BeforeAll
    void setUpAll() {
        cacheAdapter = new CacheAdapter();
        cacheAdapter.init();
    }

    @AfterAll
    void tearDownAll() {
        cacheAdapter.shutdown();
    }

    // Helper class for testing complex objects
    record TestObject(String name, int value) {
    }

    @Nested
    @DisplayName("Set and Get Operations")
    class SetAndGetOperations {

        @Test
        @DisplayName("Should store and retrieve value")
        void shouldStoreAndRetrieveValue() {
            // Act
            cacheAdapter.set(TEST_NAMESPACE, TEST_KEY, TEST_VALUE);
            String result = cacheAdapter.get(TEST_NAMESPACE, TEST_KEY);

            // Assert
            assertThat(result).isEqualTo(TEST_VALUE);
        }

        @Test
        @DisplayName("Should store and retrieve complex object")
        void shouldStoreAndRetrieveComplexObject() {
            // Arrange
            TestObject testObject = new TestObject("test", 123);

            UUID key = UUID.randomUUID();
            // Act
            cacheAdapter.set(TEST_NAMESPACE, key, testObject);
            TestObject result = cacheAdapter.get(TEST_NAMESPACE, key);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("test");
            assertThat(result.value()).isEqualTo(123);
        }

        @Test
        @DisplayName("Should store value with custom TTL")
        void shouldStoreValueWithCustomTTL() {
            // Act
            UUID ttlKey = UUID.randomUUID();
            cacheAdapter.set(TEST_NAMESPACE, ttlKey, "ttlValue", Duration.ofMinutes(5));
            String result = cacheAdapter.get(TEST_NAMESPACE, ttlKey);

            // Assert
            assertThat(result).isEqualTo("ttlValue");
        }

        @Test
        @DisplayName("Should return null for non-existent key")
        void shouldReturnNullForNonExistentKey() {
            // Act
            String result = cacheAdapter.get(TEST_NAMESPACE, UUID.randomUUID());

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Clear Operations")
    class ClearOperations {

        @Test
        @DisplayName("Should clear specific key")
        void shouldClearSpecificKey() {
            // Arrange
            UUID clearKey = UUID.randomUUID();
            cacheAdapter.set(TEST_NAMESPACE, clearKey, "clearValue");

            // Act
            cacheAdapter.clear(TEST_NAMESPACE, clearKey);
            String result = cacheAdapter.get(TEST_NAMESPACE, clearKey);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should clear all keys in namespace")
        void shouldClearAllKeysInNamespace() {
            // Arrange
            UUID key1 = UUID.randomUUID();
            UUID key2 = UUID.randomUUID();
            cacheAdapter.set("namespace1", key1, "value1");
            cacheAdapter.set("namespace1", key2, "value2");
            cacheAdapter.set("namespace2", key1, "value3");

            // Act
            cacheAdapter.clearNamespace("namespace1");

            // Assert
            assertThat(cacheAdapter.<String>get("namespace1", key1)).isNull();
            assertThat(cacheAdapter.<String>get("namespace1", key2)).isNull();
            assertThat(cacheAdapter.<String>get("namespace2", key1)).isEqualTo("value3");
        }

        @Test
        @DisplayName("Should clear all keys with suffix in namespace")
        void shouldClearAllKeysInSuffixNamespace() throws InterruptedException {
            // Arrange
            UUID user123 = UUID.randomUUID();
            UUID user456 = UUID.randomUUID();
            cacheAdapter.set("ns1", user123, "data1");
            cacheAdapter.set("ns1", user456, "data2");
            cacheAdapter.set("ns2", user123, "data3");

            // Act
            cacheAdapter.clearSuffix("ns1", user123);
            // Wait for the background thread to complete
            Thread.sleep(50);
            // Assert
            assertThat(cacheAdapter.<String>get("ns1", user123)).isNull();
            assertThat(cacheAdapter.<String>get("ns1", user456)).isEqualTo("data2");
            assertThat(cacheAdapter.<String>get("ns2", user123)).isEqualTo("data3");
        }

        @Test
        @DisplayName("Should clear all entries with specific key suffix")
        void shouldClearAllEntriesWithKeySuffix() {
            // Arrange
            UUID user123 = UUID.randomUUID();
            UUID user456 = UUID.randomUUID();
            cacheAdapter.set("ns1", user123, "data1");
            cacheAdapter.set("ns2", user123, "data2");
            cacheAdapter.set("ns3", user456, "data3");

            // Act
            cacheAdapter.clearAll(user123);

            // Assert
            assertThat(cacheAdapter.<String>get("ns1", user123)).isNull();
            assertThat(cacheAdapter.<String>get("ns2", user123)).isNull();
            assertThat(cacheAdapter.<String>get("ns3", user456)).isEqualTo("data3");
        }
    }

    @Nested
    @DisplayName("Namespace Tests")
    class NamespaceTests {

        @Test
        @DisplayName("Should isolate values by namespace")
        void shouldIsolateValuesByNamespace() {
            // Act
            UUID sameKey = UUID.randomUUID();
            cacheAdapter.set("namespace1", sameKey, "value1");
            cacheAdapter.set("namespace2", sameKey, "value2");

            // Assert
            assertThat(cacheAdapter.<String>get("namespace1", sameKey)).isEqualTo("value1");
            assertThat(cacheAdapter.<String>get("namespace2", sameKey)).isEqualTo("value2");
        }

        @Test
        @DisplayName("Should handle empty namespace")
        void shouldHandleEmptyNamespace() {
            // Act
            UUID key = UUID.randomUUID();
            cacheAdapter.set("", key, "value");
            String result = cacheAdapter.get("", key);

            // Assert
            assertThat(result).isEqualTo("value");
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update existing value")
        void shouldUpdateExistingValue() {
            // Arrange
            UUID updateKey = UUID.randomUUID();
            cacheAdapter.set(TEST_NAMESPACE, updateKey, "oldValue");

            // Act
            cacheAdapter.set(TEST_NAMESPACE, updateKey, "newValue");
            String result = cacheAdapter.get(TEST_NAMESPACE, updateKey);

            // Assert
            assertThat(result).isEqualTo("newValue");
        }
    }
}
