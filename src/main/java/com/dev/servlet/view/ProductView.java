package com.dev.servlet.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.controllers.ProductController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Product;
import com.dev.servlet.domain.User;
import com.dev.servlet.dto.ProductDTO;
import com.dev.servlet.utils.CurrencyFormatter;
import com.dev.servlet.view.base.BaseRequest;

public class ProductView extends BaseRequest {

	private static final String FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP = "forward:pages/product/formListProduct.jsp";
	private static final String FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP = "forward:pages/product/listProducts.jsp";
	private static final String FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP = "forward:pages/product/formUpdateProduct.jsp";
	private static final String FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP = "forward:pages/product/formCreateProduct.jsp";

	private static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS = "redirect:productView?action=list";
	private static final String REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID = "redirect:productView?action=list&id=";

	private final ProductController controller = new ProductController(em);
	private final CategoryController categoryController = new CategoryController(em);

	public ProductView() {
		super();
	}

	@Override
	public String execute(HttpServletRequest req, HttpServletResponse resp) {
		return switch (req.getParameter(ACTION)) {
		case CREATE -> doCreate(req, resp);
		case LIST -> doList(req, resp);
		case UPDATE -> doUpdate(req, resp);
		case EDIT -> doEdit(req, resp);
		case DELETE -> doDelete(req, resp);
		case NEW -> FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP;
		default -> FORWARD_PAGES_NOT_FOUND_JSP;
		};
	}

	public String doCreate(HttpServletRequest req, HttpServletResponse resp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate parsedDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

		Product product = new Product(req.getParameter("name"), req.getParameter("description"),
				req.getParameter("url"), parsedDate, CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));

		product.setUser(((User) req.getSession().getAttribute(USER_LOGGED)));
		product.setCategory(new Category(Long.parseLong(req.getParameter("category"))));
		controller.save(product);
		req.setAttribute("product", product);

		return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
	}

	public String doEdit(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		if (id != null) {
			req.setAttribute("error", "id can't be null");
			return FORWARD_PAGES_NOT_FOUND_JSP;
		}

		Product product = new Product();
		product.setId(Long.parseLong(id));
		product = controller.findById(product.getId());

		req.setAttribute("product", new ProductDTO(product));
		req.setAttribute("categories", categoryController.findAll(null));

		return FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP;
	}

	public String doList(HttpServletRequest req, HttpServletResponse resp) {
		Product product = new Product();

		String id = req.getParameter("id");
		if (id != null) {
			product = controller.findById(Long.valueOf(id));
			if (Objects.isNull(product)) {
				return FORWARD_PAGES_NOT_FOUND_JSP;
			}

			req.setAttribute("product", product);
			return FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP;
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

		product.setUser((User) req.getAttribute(USER_LOGGED));
		List<Product> products = controller.findAll(product);

		req.setAttribute("products", products);
		req.setAttribute("categories", categoryController.findAll(null));

		return FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP;
	}

	public String add(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("categories", categoryController.findAll(null));
		return FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP;
	}

	public String doUpdate(HttpServletRequest req, HttpServletResponse resp) {

		Product product = controller.findById(Long.parseLong(req.getParameter("id")));
		product.setName(req.getParameter("name"));
		product.setDescription(req.getParameter("description"));
		product.setPrice(CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));
		product.setUrl(req.getParameter("url"));
		product.setCategory(new Category(Long.parseLong(req.getParameter("category"))));

		controller.update(product);
		req.setAttribute("product", product);

		return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
	}

	public String doDelete(HttpServletRequest req, HttpServletResponse resp) {
		Product product = new Product();
		product.setId(Long.parseLong(req.getParameter("id")));
		controller.delete(product);
		return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
	}
}
