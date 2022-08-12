package servlets.product;

import domain.Product;
import dto.ProductDTO;
import org.apache.commons.lang3.time.StopWatch;
import utils.CurrencyFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static servlets.base.Base.*;

public class ProductServlet extends BaseProduct {

    /**
     * Execute.
     *
     * @param req  the req
     * @param resp the resp
     * @return the string
     */

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("action") == null) {
            logger.error("Error: action can't be null");
            req.setAttribute(ERROR, "Action can't be null");
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        switch (req.getParameter("action")) {
            case "create" -> {
                return create(req, resp);
            }
            case "list" -> {
                return list(req, resp);
            }
            case "update" -> {
                return update(req, resp);
            }
            case "new" -> {
                return add();
            }
            case "edit" -> {
                return edit(req, resp);
            }
            case "delete" -> {
                return delete(req, resp);
            }
            default -> {
                logger.error("Error: action not found");
                req.setAttribute(ERROR, "Action not found");
            }
        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }


    public String create(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info("doPOST creating product");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate parsedDate = LocalDate.parse(LocalDate.now().format(formatter), formatter);

        Product product = new Product(
                req.getParameter(NAME),
                req.getParameter(DESCRIPTION),
                req.getParameter(URL),
                parsedDate,
                CurrencyFormatter.stringToBigDecimal(req.getParameter(PRICE)));

        Product saved = controller.save(product);
        req.setAttribute(PRODUCT, saved);
        sw.stop();
        logger.info("doPOST product created in {} ms", sw.getTime());
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + saved.getId();
    }

    public String edit(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST editing product");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = controller.findById(Long.parseLong(req.getParameter(ID)));
        req.setAttribute(PRODUCT, new ProductDTO(product));
        return FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP;
    }

    private String list(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST listing products by filter");

        String id = req.getParameter(ID);
        if (!Objects.isNull(id)) {
            Product product = controller.findById(Long.parseLong(id));
            if (Objects.isNull(product)) {
                return FORWARD_PAGES_NOT_FOUND_JSP;
            }

            req.setAttribute(PRODUCT, product);
            return FORWARD_PAGES_PRODUCT_FORM_LIST_PRODUCT_JSP;
        }

        String param = req.getParameter("param");
        String value = req.getParameter("value");
        List<Product> products;
        if (!Objects.isNull(param) && !Objects.isNull(value)) {
            if (param.equals(NAME)) {
                products = controller.findAllByName(value);
                req.setAttribute(NAME, true);
            } else {
                products = controller.findAllByDescription(value);
                req.setAttribute(DESCRIPTION, true);
            }
            req.setAttribute(PRODUCTS, products);
        } else {
            products = controller.findAll();
            req.setAttribute(PRODUCTS, products);
        }
        return FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP;
    }

    private String add() {
        logger.info("doPOST redirecting to form createProduct");
        return FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP;
    }

    private String update(HttpServletRequest req, HttpServletResponse resp) {
        StopWatch sw = new StopWatch();
        sw.start();
        logger.info("doPOST updating product");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = controller.findById(Long.parseLong(req.getParameter(ID)));
        product.setName(req.getParameter(NAME));
        product.setDescription(req.getParameter(DESCRIPTION));
        product.setPrice(CurrencyFormatter.stringToBigDecimal(req.getParameter(PRICE)));
        product.setUrl(req.getParameter(URL));
        controller.update(product);
        req.setAttribute(PRODUCT, product);
        sw.stop();
        logger.info("doPOST product updated in {} ms", sw.getTime());
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
    }

    private String delete(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doPOST deleting product");

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
    }
}
