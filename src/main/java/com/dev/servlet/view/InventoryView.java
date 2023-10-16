package com.dev.servlet.view;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.servlet.controllers.InventoryController;
import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.Product;
import com.dev.servlet.view.base.BaseRequest;

public class InventoryView extends BaseRequest {

	private static final String FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP = "forward:pages/inventory/formListItem.jsp";
	private static final String FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP = "forward:pages/inventory/listItems.jsp";
	private static final String FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP = "forward:pages/inventory/formCreateItem.jsp";
	private static final String FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP = "forward:pages/inventory/formUpdateItem.jsp";

	private static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS = "redirect:inventoryView?action=list";
	private static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID = "redirect:inventoryView?action=list&id=";

	private final InventoryController controller = new InventoryController(em);

	public InventoryView() {
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
		case NEW -> FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP;
		default -> FORWARD_PAGES_NOT_FOUND_JSP;
		};
	}

	/**
	 * Create the item.
	 *
	 * @param req  the request
	 * @param resp the response
	 * @return string
	 */
	public String doCreate(HttpServletRequest req, HttpServletResponse resp) {
		Product product = new Product();
		// TODO getProduct from request
		int quantity = Integer.parseInt(req.getParameter("quantity"));

		Inventory item = new Inventory(product, quantity, req.getParameter("description"));
		controller.save(item);

		req.setAttribute("item", item);
		return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
	}

	/**
	 * list item or items.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doList(HttpServletRequest req, HttpServletResponse resp) {
		String id = req.getParameter("id");
		if (id != null) {
			Inventory inventory = controller.findById(Long.parseLong(id));
			if (Objects.isNull(inventory)) {
				return FORWARD_PAGES_NOT_FOUND_JSP;
			}

			req.setAttribute("item", inventory);
			return FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP;
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

		return FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP;
	}

	/**
	 * update item.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doUpdate(HttpServletRequest req, HttpServletResponse resp) {
		Inventory item = controller.findById(Long.parseLong(req.getParameter("id")));
		item.setQuantity(Integer.parseInt(req.getParameter("quantity")));
		item.setDescription(req.getParameter("description"));

		Product product = new Product();
		// TODO getProduct from request

		if (Objects.isNull(product)) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			req.setAttribute("error", "ERROR: Product ID " + req.getParameter("productId") + " was not found.");
			req.setAttribute("item", item);
			return FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP;
		}

		item.setProduct(product);
		controller.update(item);
		req.setAttribute("item", item);

		return REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID + item.getId();
	}

	/**
	 * update item.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doEdit(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("item", controller.findById(Long.parseLong(req.getParameter("id"))));
		return FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP;
	}

	/**
	 * delete item.
	 *
	 * @param req  the req
	 * @param resp the resp
	 * @return the string
	 */
	public String doDelete(HttpServletRequest req, HttpServletResponse resp) {
		Inventory obj = new Inventory(Long.parseLong(req.getParameter("id")));
		controller.delete(obj);
		return REDIRECT_INVENTORY_ACTION_LIST_ITEMS;
	}
}
