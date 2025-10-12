package com.dev.servlet.controller;

class ProductControllerTest {

//    @Mock
//    private IProductService productService;
//
//    @Mock
//    private ICategoryService categoryService;
//
//    @InjectMocks
//    private ProductController productController;
//
//    @Mock
//    private Request request;
//
//    @Mock
//    private PageRequest<Object> pageRequest;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        when(request.token()).thenReturn("fakeToken");
//        when(request.query()).thenReturn(mock(Query.class));
//        when(request.query().getPageRequest()).thenReturn(pageRequest);
////        when(request.query().getPageRequestImpl().getRecords()).thenReturn(List.of(1L, 2L));
//    }
//
//    @Test
//    @DisplayName(
//            "Test create method to add a new product. " +
//            "It should return a 201 status code and the expected response.")
//    void testCreateProduct() {
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setId(UUID.randomUUID().toString());
//
//        when(productService.create(request)).thenReturn(productResponse);
//
//        IHttpResponse<Void> response = productController.create(request);
//
//        assertNotNull(response);
//        assertEquals(201, response.statusCode());
//        verify(productService, times(1)).create(request);
//    }
//
//    @Test
//    @DisplayName(
//            "Test forward method to create form. " +
//            "It should return a 302 status code and the expected response.")
//    void testForwardToCreateForm() {
//        Collection<CategoryResponse> categories = List.of(new CategoryResponse());
//        when(categoryService.list(any())).thenReturn(categories);
//
//        IHttpResponse<Collection<CategoryResponse>> response = productController.forward(request);
//
//        assertNotNull(response);
//        assertEquals(302, response.statusCode());
//        assertEquals(categories, response.body());
//        verify(categoryService, times(1)).list(any());
//    }
//
//    @Test
//    @DisplayName(
//            "Test edit method to update a product. " +
//            "It should return a 200 status code and the expected response.")
//    void testEditProduct() throws ServiceException {
//        ProductResponse productResponse = new ProductResponse();
//        Collection<CategoryResponse> categories = List.of(new CategoryResponse());
//
//        when(categoryService.list(any())).thenReturn(categories);
//        when(productService.findById(request)).thenReturn(productResponse);
//
//        IServletResponse response = productController.edit(request);
//
//        assertNotNull(response);
//        verify(productService, times(1)).findById(request);
//        verify(categoryService, times(1)).list(any());
//    }
//
//    @Test
//    @DisplayName(
//            "Test listProducts method to retrieve a list of products. " +
//            "It should return a 200 status code and the expected response.")
//    @SuppressWarnings("unchecked")
//    void testListProducts() {
//        // Setup
//        Product filterMock = new Product("prod", "desc", null);
//        when(productService.getBody(any())).thenReturn(filterMock);
//
//        var categories = List.of(new CategoryResponse());
//        when(categoryService.list(any())).thenReturn(categories);
//
//        var products = List.of(
//                ProductMapper.base(new Product("prod1", "desc1", BigDecimal.valueOf(50))),
//                ProductMapper.base(new Product("prod2", "desc2", BigDecimal.valueOf(50)))
//        );
//
//        var pageableMock = PageResponse.<ProductResponse>builder()
//                .content(products)
//                .currentPage(1)
//                .pageSize(2)
//                .sort(Sort.by("id").ascending())
//                .build();
//
//        when(productService.getAllPageable(any(), any(Mapper.class))).thenReturn(pageableMock);
//        when(productService.calculateTotalPriceFor(any())).thenReturn(BigDecimal.valueOf(100));
//
//        // Execution
//        IServletResponse response = productController.list(request);
//
//        // Verification
//        assertNotNull(response);
//        assertEquals(200, response.statusCode());
//
//        // Verify pageable content
//        var pageable = response.body().stream()
//                .filter(pair -> "pageable".equals(pair.key()))
//                .findFirst()
//                .map(e -> (IPageable<ProductResponse>) e.value())
//                .orElseThrow(() -> new AssertionError("Pageable not found"));
//
//        long counter = pageable.getContent().size();
//        assertEquals(2, counter);
//
//        // Verify total price
//        BigDecimal totalPrice = response.body().stream()
//                .filter(pair -> "totalPrice".equals(pair.key()))
//                .findFirst()
//                .map(e -> (BigDecimal) e.value())
//                .orElse(null);
//
//        assertEquals(BigDecimal.valueOf(100), totalPrice);
//
//        // Verify categories
//        var responseCategories = response.body().stream()
//                .filter(pair -> "categories".equals(pair.key()))
//                .findFirst()
//                .map(e -> (Collection<CategoryResponse>) e.value())
//                .orElse(null);
//
//        assertEquals(categories, responseCategories);
//
//        // Verify interactions
//        verify(productService, times(1)).getBody(request);
//        verify(productService, times(1)).getAllPageable(any(), any(Mapper.class));
//        verify(productService, times(1)).calculateTotalPriceFor(filterMock);
//        verify(categoryService, times(1)).list(any());
//    }
//
//    @Test
//    @DisplayName(
//            "Test delete method to remove a product. " +
//            "It should return a 200 status code and the expected response.")
//    void testDeleteProduct() throws ServiceException {
//        doReturn(true).when(productService).delete(request);
//
//        IHttpResponse<Void> response = productController.delete(request);
//
//        assertNotNull(response);
//        assertEquals(200, response.statusCode());
//        verify(productService, times(1)).delete(request);
//    }
//
//    @Test
//    @DisplayName(
//            "Test listById method to retrieve a product by ID. " +
//            "It should return a 200 status code and the expected response.")
//    void testGetById() throws ServiceException {
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setId(UUID.randomUUID().toString());
//
//        when(productService.findById(request)).thenReturn(productResponse);
//
//        IHttpResponse<ProductResponse> response = productController.getById(request);
//        assertNotNull(response);
//        assertEquals(200, response.statusCode());
//        assertEquals(productResponse, response.body());
//        verify(productService, times(1)).findById(request);
//    }
}
