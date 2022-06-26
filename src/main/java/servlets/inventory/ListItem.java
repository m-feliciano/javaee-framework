package servlets.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListItem extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing a inventory item");

        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        req.setAttribute("item", controller.findById(Long.parseLong(req.getParameter("id"))));
        return "forward:pages/inventory/formListItem.jsp";
    }

}
