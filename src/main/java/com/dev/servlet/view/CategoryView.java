package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.dto.CategoryDTO;
import com.dev.servlet.view.base.BaseRequest;

public class CategoryView extends BaseRequest {

	private static final String FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP = "forward:pages/category/formCreateCategory.jsp";
	private static final String FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP = "forward:pages/category/listCategories.jsp";
	private static final String FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP = "forward:pages/category/formListCategory.jsp";
	private static final String FORWARD_PAGES_CATEGORY_FORM_UPDATE_CATEGORY_JSP = "forward:pages/category/formUpdateCategory.jsp";

	private static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORIES = "redirect:categoryView?action=list";
	private static final String REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID = "redirect:categoryView?action=list&id=";

	private static final String CATEGORY = "category";

	private final CategoryController controller = new CategoryController(em);

	public CategoryView() {
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
		case NEW -> add();
		default -> FORWARD_PAGES_NOT_FOUND_JSP;
		};
	}

	public String add() {
		return FORWARD_PAGES_CATEGORY_FORM_CREATE_CATEGORY_JSP;
	}

	/**
	 * update category.
	 *
	 * @return the string
	 */
	public String doUpdate(HttpServletRequest req, HttpServletResponse resp) {
		var category = controller.findById(Long.parseLong(req.getParameter("id")));
		category.setName(req.getParameter("name"));
		
		em.getTransaction().begin();
		controller.update(category);
		req.setAttribute(CATEGORY, new CategoryDTO(category));
		em.getTransaction().commit();
		em.close();
		return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + category.getId();
	}

	/**
	 * List category by id.
	 *
	 * @return the string
	 */
	public String doList(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		if (id != null) {
			req.setAttribute(CATEGORY, controller.findById(Long.valueOf(id)));
			return FORWARD_PAGES_CATEGORY_FORM_LIST_CATEGORY_JSP;
		}

		req.setAttribute("categories", controller.findAll(null));
		return FORWARD_PAGES_CATEGORY_LIST_CATEGORIES_JSP;
	}

	/**
	 * Edit category by id.
	 *
	 * @return the string
	 */
	public String doEdit(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute(CATEGORY, controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGES_CATEGORY_FORM_UPDATE_CATEGORY_JSP;
	}

	/**
	 * delete category by id.
	 *
	 * @return the string
	 */
	public String doDelete(HttpServletRequest req, HttpServletResponse resp) {
		em.getTransaction().begin();

		Category cat = new Category(Long.valueOf(req.getParameter("id")));
		controller.delete(cat);
		
		em.getTransaction().commit();
		em.close();
		return REDIRECT_CATEGORY_ACTION_LIST_CATEGORIES;
	}

	/**
	 * create category.
	 *
	 * @return the string
	 */
	public String doCreate(HttpServletRequest req, HttpServletResponse resp) {
		Category cat = new Category(req.getParameter("name"));
		cat.setStatus(Status.ACTIVE.getDescription());

		em.getTransaction().begin();
		controller.save(cat);
		em.getTransaction().commit();
		em.close();

		return REDIRECT_CATEGORY_ACTION_LIST_CATEGORY_BY_ID + cat.getId();
	}
}
