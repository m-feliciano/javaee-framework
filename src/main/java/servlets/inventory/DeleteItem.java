package servlets.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteItem extends BaseInventory {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET deleting item");
        if (!this.validate(req, resp)) {
            return "forward:pages/not-found.jsp";
        }

        controller.delete(Long.parseLong(req.getParameter("id")));
        return "redirect:inventory?action=ListItems";
    }

}
