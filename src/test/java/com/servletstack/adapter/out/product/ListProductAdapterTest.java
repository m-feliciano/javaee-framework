package com.servletstack.adapter.out.product;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.application.transfer.response.ProductResponse;
import com.servletstack.domain.entity.Product;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import com.servletstack.infrastructure.persistence.transfer.internal.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListProductAdapter Tests")
class ListProductAdapterTest {

    private static final String AUTH_TOKEN = "Bearer valid.token";

    @Mock
    private ProductRepositoryPort repositoryPort;
    @Mock
    private PageRequest pageRequest;
    @Mock
    private IPageable<?> pageableResponse;
    @Mock
    private Mapper<Product, ProductResponse> mapper;

    @InjectMocks
    private ListProductAdapter listProductAdapter;

    @BeforeEach
    @SuppressWarnings("all")
    void setUp() {

        lenient()
                .when(repositoryPort.getAllPageable(any(IPageRequest.class), any()))
                .thenReturn((IPageable<Object>) pageableResponse);
    }

    @Nested
    @DisplayName("List Products Tests")
    class ListProductsTests {

        @Test
        @DisplayName("Should fetch products pageable")
        void shouldFetchProductsPageable() {
            // Act
            IPageable<ProductResponse> result = listProductAdapter.getAllPageable(pageRequest, AUTH_TOKEN, mapper);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(pageableResponse);
            verify(repositoryPort).getAllPageable(pageRequest, mapper);
        }

        @Test
        @DisplayName("Should delegate to repository with correct parameters")
        void shouldDelegateToRepositoryWithCorrectParameters() {
            // Act
            listProductAdapter.getAllPageable(pageRequest, AUTH_TOKEN, mapper);

            // Assert
            verify(repositoryPort).getAllPageable(eq(pageRequest), eq(mapper));
        }

        @Test
        @DisplayName("Should return pageable response")
        void shouldReturnPageableResponse() {
            // Act
            IPageable<ProductResponse> result = listProductAdapter.getAllPageable(pageRequest, AUTH_TOKEN, mapper);

            // Assert
            assertThat(result).isSameAs(pageableResponse);
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should handle first page request")
        void shouldHandleFirstPageRequest() {
            // Arrange
            IPageRequest firstPageRequest = mock(IPageRequest.class);

            // Act
            listProductAdapter.getAllPageable(firstPageRequest, AUTH_TOKEN, mapper);

            // Assert
            verify(repositoryPort).getAllPageable(firstPageRequest, mapper);
        }

        @Test
        @DisplayName("Should handle different page sizes")
        void shouldHandleDifferentPageSizes() {
            // Arrange
            IPageRequest customPageRequest = mock(IPageRequest.class);

            // Act
            listProductAdapter.getAllPageable(customPageRequest, AUTH_TOKEN, mapper);

            // Assert
            verify(repositoryPort).getAllPageable(customPageRequest, mapper);
        }
    }

    @Nested
    @DisplayName("Mapper Tests")
    class MapperTests {

        @Test
        @DisplayName("Should use provided mapper")
        void shouldUseProvidedMapper() {
            // Act
            listProductAdapter.getAllPageable(pageRequest, AUTH_TOKEN, mapper);

            // Assert
            verify(repositoryPort).getAllPageable(any(IPageRequest.class), eq(mapper));
        }

        @Test
        @DisplayName("Should work with custom mapper")
        void shouldWorkWithCustomMapper() {
            // Arrange
            @SuppressWarnings("unchecked")
            Mapper<Product, ProductResponse> customMapper = mock(Mapper.class);

            // Act
            listProductAdapter.getAllPageable(pageRequest, AUTH_TOKEN, customMapper);

            // Assert
            verify(repositoryPort).getAllPageable(pageRequest, customMapper);
        }
    }
}

