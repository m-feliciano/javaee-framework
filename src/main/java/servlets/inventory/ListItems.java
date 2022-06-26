package servlets.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListItems extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing items");
        req.setAttribute("items", controller.findAll());
        return "forward:pages/inventory/listItems.jsp";
    }

}
