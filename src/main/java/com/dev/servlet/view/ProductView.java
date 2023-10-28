package com.dev.servlet.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Product;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.utils.CacheUtil;
import com.dev.servlet.utils.CurrencyFormatter;
import com.dev.servlet.view.base.BaseRequest;

public class ProductView extends BaseRequest {

	private static final String FORWARD_PAGE_LIST = "forward:pages/product/formListProduct.jsp";
	private static final String FORWARD_PAGE_LIST_PRODUCTS = "forward:pages/product/listProducts.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/product/formUpdateProduct.jsp";
	private static final String FORWARD_PAGE_CREATE = "forward:pages/product/formCreateProduct.jsp";

	private static final String REDIRECT_ACTION_LIST_ALL = "redirect:productView?action=list";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:productView?action=list&id=";

	private ProductController controller;
	private CategoryController categoryController;

	public ProductView() {
		super();
	}

	public ProductView(EntityManager em) {
		super();
		this.categoryController = new CategoryController(em);
		this.controller = new ProductController(em);

	}

	/**
	 * Forward
	 *
	 * @return the next path
	 */
	@ResourcePath(value = NEW, forward = true)
	public String forwardRegister() {
		return FORWARD_PAGE_CREATE;
	}

	@ResourcePath(value = CREATE)
	public String registerOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate parsedDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

		Product product = new Product(req.getParameter("name"), req.getParameter("description"),
				req.getParameter("url"), parsedDate, CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));

		String token = (String) req.getSession().getAttribute("token");
		product.setUser(CacheUtil.getUser(token));
		product.setCategory(new Category(Long.parseLong(req.getParameter("category"))));
		controller.save(product);
		req.setAttribute("product", product);

		return REDIRECT_ACTION_LIST_BY_ID + product.getId();
	}

	@ResourcePath(value = EDIT)
	public String editOne(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		if (id != null) {
			req.setAttribute("error", "id can't be null");
			return FORWARD_PAGES_NOT_FOUND;
		}

		Product product = new Product();
		product.setId(Long.parseLong(id));
		product = controller.findById(product.getId());

		req.setAttribute("product", product);
		req.setAttribute("categories", categoryController.findAll(null));

		return FORWARD_PAGE_UPDATE;
	}

	@ResourcePath(value = LIST)
	public String findAll(BusinessRequest businessRequest) {

		HttpServletRequest req = businessRequest.getRequest();
		Product product = new Product();

		String id = req.getParameter("id");
		if (id != null) {
			product = controller.findById(Long.valueOf(id));
			if (Objects.isNull(product)) {
				return FORWARD_PAGES_NOT_FOUND;
			}

			req.setAttribute("product", product);
			return FORWARD_PAGE_LIST;
		}

		String param = req.getParameter(PARAM);
		String value = req.getParameter(VALUE);
		if (param != null && value != null) {
			product = new Product();

			if (param.equals("name")) {
				product.setName(value);
			} else {
				product.setDescription(value);
			}

			if (!Objects.isNull(req.getParameter("category"))) {
				Category filter = new Category(Long.parseLong(req.getParameter("category")));
				product.setCategory(filter);
			}
		}

		String token = (String) req.getSession().getAttribute("token");
		product.setUser(CacheUtil.getUser(token));
		List<Product> products = controller.findAll(product);

		req.setAttribute("products", products);
		req.setAttribute("categories", categoryController.findAll(null));

		return FORWARD_PAGE_LIST_PRODUCTS;
	}

	@ResourcePath(value = UPDATE)
	public String updateOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Product product = controller.findById(Long.parseLong(req.getParameter("id")));
		product.setName(req.getParameter("name"));
		product.setDescription(req.getParameter("description"));
		product.setPrice(CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));
		product.setUrl(req.getParameter("url"));
		product.setCategory(new Category(Long.parseLong(req.getParameter("category"))));

		controller.update(product);
		req.setAttribute("product", product);

		return REDIRECT_ACTION_LIST_BY_ID + product.getId();
	}

	@ResourcePath(value = EDIT)
	public String deleteOne(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Product product = new Product();
		product.setId(Long.parseLong(req.getParameter("id")));
		controller.delete(product);
		return REDIRECT_ACTION_LIST_ALL;
	}
}
