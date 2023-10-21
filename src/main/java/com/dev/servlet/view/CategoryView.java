package com.dev.servlet.view;

import javax.servlet.http.HttpServletRequest;

import com.dev.servlet.controllers.CategoryController;
import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.Status;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.interfaces.ResquestPath;
import com.dev.servlet.view.base.BaseRequest;

public class CategoryView extends BaseRequest {

	private static final String FORWARD_PAGE_CREATE = "forward:pages/category/formCreateCategory.jsp";
	private static final String FORWARD_PAGE_LIST = "forward:pages/category/listCategories.jsp";
	private static final String FORWARD_PAGE_LIST_BY_ID = "forward:pages/category/formListCategory.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/category/formUpdateCategory.jsp";

	private static final String REDIRECT_ACTION_LIST_ALL = "redirect:categoryView?action=list";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:categoryView?action=list&id=";

	private static final String CATEGORY = "category";

	private final CategoryController controller = new CategoryController(em);

	public CategoryView() {
		super();
	}

	/**
	 * forward form create
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = NEW)
	public String forwardCreate(BusinessRequest businessRequest) {
		return FORWARD_PAGE_CREATE;
	}

	/**
	 * update category.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = UPDATE)
	public String doUpdate(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		var category = controller.findById(Long.parseLong(req.getParameter("id")));
		category.setName(req.getParameter("name"));
		controller.update(category);
		req.setAttribute(CATEGORY, category);
		return REDIRECT_ACTION_LIST_BY_ID + category.getId();
	}

	/**
	 * List category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = LIST)
	public String doList(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		String id = req.getParameter("id");
		if (id != null) {
			req.setAttribute(CATEGORY, controller.findById(Long.valueOf(id)));
			return FORWARD_PAGE_LIST_BY_ID;
		}

		req.setAttribute("categories", controller.findAll(null));
		return FORWARD_PAGE_LIST;
	}

	/**
	 * Edit category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = EDIT)
	public String doEdit(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		req.setAttribute(CATEGORY, controller.findById(Long.valueOf(req.getParameter("id"))));
		return FORWARD_PAGE_UPDATE;
	}

	/**
	 * delete category by id.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = DELETE)
	public String doDelete(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Category cat = new Category(Long.valueOf(req.getParameter("id")));
		controller.delete(cat);
		return REDIRECT_ACTION_LIST_ALL;
	}

	/**
	 * create category.
	 * 
	 ** @param businessRequest
	 * @return the string
	 */
	@ResquestPath(value = CREATE)
	public String doCreate(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Category cat = new Category(req.getParameter("name"));
		cat.setStatus(Status.ACTIVE.getDescription());
		controller.save(cat);
		return REDIRECT_ACTION_LIST_BY_ID + cat.getId();
	}
}
