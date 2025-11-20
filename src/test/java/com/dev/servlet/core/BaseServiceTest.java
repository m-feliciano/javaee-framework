package com.dev.servlet.core;

import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.Properties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public abstract class BaseServiceTest {
    protected static final String TEST_TOKEN = "test-bearerToken-with-sufficient-length-for-cache";

    protected static MockedStatic<Properties> propertiesUtilMock;
    protected static MockedStatic<CacheUtils> cacheUtilsMock;

    @BeforeAll
    static void setUpBaseClass() {
        propertiesUtilMock = mockStatic(Properties.class);
        propertiesUtilMock.when(() -> Properties.get(anyString())).thenReturn("1");
        propertiesUtilMock.when(() -> Properties.getOrDefault(anyString(), any())).thenReturn(1L);

        cacheUtilsMock = mockStatic(CacheUtils.class);
    }

    @AfterAll
    static void tearDownBaseClass() {
        if (propertiesUtilMock != null) {
            propertiesUtilMock.close();
        }

        if (cacheUtilsMock != null) {
            cacheUtilsMock.close();
        }
    }
}