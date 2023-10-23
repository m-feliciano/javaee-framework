package com.dev.servlet.view;

import javax.persistence.EntityManager;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResourcePath;
import com.dev.servlet.view.base.BaseRequest;

public class CategoryView extends BaseRequest {

	private static final String FORWARD_PAGE_CREATE = "forward:pages/category/formCreateCategory.jsp";
	private static final String FORWARD_PAGE_LIST = "forward:pages/category/listCategories.jsp";
	private static final String FORWARD_PAGE_LIST_BY_ID = "forward:pages/category/formListCategory.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/category/formUpdateCategory.jsp";

	private static final String REDIRECT_ACTION_LIST_ALL = "redirect:categoryView?action=list";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:categoryView?action=list&id=";

	private static final String CATEGORY = "category";

	private CategoryController controller;

	public CategoryView() {
		super();
	}

	public CategoryView(EntityManager em) {
		super();
		this.controller = new CategoryController(em);
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

	/**
	 * create category.
	 *
	 ** @param businessRequest
	 * @return the string
	 */
	@ResourcePath(value = CREATE)
	public String registerOne(BusinessRequest businessRequest) {
		var request = businessRequest.getRequest();

		Category cat = new Category(request.getParameter("name"));
		cat.setStatus(Status.ACTIVE.getDescription());
		controller.save(cat);
		return REDIRECT_ACTION_LIST_BY_ID + cat.getId();
	}

	/**
	 * update category.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResourcePath(value = UPDATE)
	public String updateOne(BusinessRequest businessRequest) {
		var request = businessRequest.getRequest();

		var category = controller.findById(Long.parseLong(request.getParameter("id")));
		category.setName(request.getParameter("name"));
		controller.update(category);
		request.setAttribute(CATEGORY, category);
		return REDIRECT_ACTION_LIST_BY_ID + category.getId();
	}

	/**
	 * List category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResourcePath(value = LIST)
	public String findAll(BusinessRequest businessRequest) {
		var request = businessRequest.getRequest();

		String id = request.getParameter("id");
		if (id != null) {
			request.setAttribute(CATEGORY, controller.findById(Long.valueOf(id)));
			return FORWARD_PAGE_LIST_BY_ID;
		}

		request.setAttribute("categories", controller.findAll(null));
		return FORWARD_PAGE_LIST;
	}

	/**
	 * Edit category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResourcePath(value = EDIT)
	public String editOne(BusinessRequest businessRequest) {
		var request = businessRequest.getRequest();
		request.setAttribute(CATEGORY, controller.findById(Long.valueOf(request.getParameter("id"))));
		return FORWARD_PAGE_UPDATE;
	}

	/**
	 * delete category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResourcePath(value = DELETE)
	public String deleteOne(BusinessRequest businessRequest) {
		var request = businessRequest.getRequest();

		Category cat = new Category(Long.valueOf(request.getParameter("id")));
		controller.delete(cat);
		return REDIRECT_ACTION_LIST_ALL;
	}
}
