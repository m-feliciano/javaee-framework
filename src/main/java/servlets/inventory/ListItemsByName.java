package servlets.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class ListItemsByName extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing items by name");

        String name = req.getParameter("name");
        if (Objects.isNull(name)) {
            return "forward:pages/not-found.jsp";
        }

        req.setAttribute("items", controller.findAllByProductName(name));
        return "forward:pages/inventory/listItems.jsp";
    }

}
