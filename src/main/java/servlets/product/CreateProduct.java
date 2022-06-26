package servlets.product;

import domain.Product;
import utils.CurrencyFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateProduct extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating product");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate parsedDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

        Product product = new Product(
                req.getParameter("name"),
                req.getParameter("description"),
                req.getParameter("url"),
                parsedDate,
                CurrencyFormatter.stringToBigDecimal(req.getParameter("price")));

        Product saved = controller.save(product);
        req.setAttribute("product", saved);
        return "redirect:product?action=ListProduct&id=" + saved.getId();
    }

}
