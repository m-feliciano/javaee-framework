package com.dev.servlet.view;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.filter.BusinessRequest;
import com.dev.servlet.view.base.BaseRequest;

public class InventoryView extends BaseRequest {

	private static final String FORWARD_PAGE_LIST = "forward:pages/inventory/formListItem.jsp";
	private static final String FORWARD_PAGE_LIST_ITEMS = "forward:pages/inventory/listItems.jsp";
	private static final String FORWARD_PAGE_CREATE = "forward:pages/inventory/formCreateItem.jsp";
	private static final String FORWARD_PAGE_UPDATE = "forward:pages/inventory/formUpdateItem.jsp";

	private static final String REDIRECT_ACTION_LIST_ALL = "redirect:inventoryView?action=list";
	private static final String REDIRECT_ACTION_LIST_BY_ID = "redirect:inventoryView?action=list&id=";

	private final InventoryController controller = new InventoryController(em);

	public InventoryView() {
		super();
	}

	/**
	 * Forward page form
	 *
	 * @param
	 * @return the next path
	 */
	public String forward(BusinessRequest businessRequest) {
		return FORWARD_PAGE_CREATE;
	}

	/**
	 * Create the item.
	 *
	 * @param businessRequest
	 * @return the next path
	 */
	public String doCreate(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Product product = new Product();
		// TODO getProduct from request
		int quantity = Integer.parseInt(req.getParameter("quantity"));

		Inventory item = new Inventory(product, quantity, req.getParameter("description"));
		controller.save(item);

		req.setAttribute("item", item);
		return REDIRECT_ACTION_LIST_BY_ID + item.getId();
	}

	/**
	 * list item or items.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	public String doList(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		String id = req.getParameter("id");
		if (id != null) {
			Inventory inventory = controller.findById(Long.parseLong(id));
			if (Objects.isNull(inventory)) {
				return FORWARD_PAGES_NOT_FOUND;
			}

			req.setAttribute("item", inventory);
			return FORWARD_PAGE_LIST;
		}

		String param = req.getParameter(PARAM);
		String value = req.getParameter(VALUE);
		List<Inventory> inventories;
		if (!Objects.isNull(param) && !Objects.isNull(value)) {
			if (param.equals("name")) {
				req.setAttribute("name", param);
				inventories = controller.findAllByProductName(value);
			} else {
				req.setAttribute("description", param);
				inventories = controller.findAllByDescription(value);
			}
			req.setAttribute("items", inventories);
		} else {
			inventories = controller.findAll(null);
			req.setAttribute("items", inventories);
		}

		return FORWARD_PAGE_LIST_ITEMS;
	}

	/**
	 * update item.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	public String doUpdate(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Inventory item = controller.findById(Long.parseLong(req.getParameter("id")));
		item.setQuantity(Integer.parseInt(req.getParameter("quantity")));
		item.setDescription(req.getParameter("description"));

		Product product = new Product();
		// TODO getProduct from request

		if (Objects.isNull(product)) {
			businessRequest.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
			req.setAttribute("error", "ERROR: Product ID " + req.getParameter("productId") + " was not found.");
			req.setAttribute("item", item);
			return FORWARD_PAGE_UPDATE;
		}

		item.setProduct(product);
		controller.update(item);
		req.setAttribute("item", item);

		return REDIRECT_ACTION_LIST_BY_ID + item.getId();
	}

	/**
	 * update item.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	public String doEdit(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();
		req.setAttribute("item", controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGE_UPDATE;
	}

	/**
	 * delete item.
	 *
	 * @param businessRequest
	 * @return the string
	 */
	public String doDelete(BusinessRequest businessRequest) {
		HttpServletRequest req = businessRequest.getRequest();

		Inventory obj = new Inventory(Long.parseLong(req.getParameter("id")));
		controller.delete(obj);
		return REDIRECT_ACTION_LIST_ALL;
	}
}
