package servlets.inventory;

import com.mchange.util.AssertException;
import controllers.InventoryController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.Action;
import servlets.utils.RequestValidation;
import utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class BaseInventory implements Action, RequestValidation {

    protected final EntityManager em = JPAUtil.getEntityManager();
    protected final InventoryController controller = new InventoryController(em);
    protected final Logger logger = LoggerFactory.getLogger(BaseInventory.class);

    protected static final String FORWARD_PAGES_INVENTORY_FORM_LIST_ITEM_JSP = "forward:pages/inventory/formListItem.jsp";
    protected static final String FORWARD_PAGES_INVENTORY_LIST_ITEMS_JSP = "forward:pages/inventory/listItems.jsp";
    protected static final String FORWARD_PAGES_INVENTORY_FORM_CREATE_ITEM_JSP = "forward:pages/inventory/formCreateItem.jsp";
    protected static final String FORWARD_PAGES_INVENTORY_FORM_UPDATE_ITEM_JSP = "forward:pages/inventory/formUpdateItem.jsp";
    protected static final String FORWARD_PAGES_NOT_FOUND_JSP = "forward:pages/not-found.jsp";
    protected static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS = "redirect:inventory?action=ListItems";
    protected static final String REDIRECT_INVENTORY_ACTION_LIST_ITEMS_BY_ID = "redirect:inventory?action=ListItems&id=";
    protected static final String ID = "id";
    protected static final String ITEM = "item";
    protected static final String ITEMS = "items";
    protected static final String QUANTITY = "quantity";
    protected static final String DESCRIPTION = "description";
    protected static final String PRODUCT_ID = "productId";
    public static final String ERROR = "error";

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.error("Error: BaseProduct.execute() is not implemented");
        throw new AssertException("This method should be overridden");
    }

    @Override
    public boolean validate(HttpServletRequest req, HttpServletResponse resp) {
        if (Objects.isNull(req.getParameter(ID))) {
            logger.warn("Error: Id can't be null");
            req.setAttribute(ERROR, "Item not found");
            return false;
        }
        return true;
    }

}
