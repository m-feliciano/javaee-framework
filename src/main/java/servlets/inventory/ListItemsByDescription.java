package servlets.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListItemsByDescription extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing items by description");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        req.setAttribute("items", controller.findByDescription(req.getParameter("description")));
        return "forward:pages/inventory/listItems.jsp";
    }

}
