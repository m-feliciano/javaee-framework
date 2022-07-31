package servlets.product;

import domain.Product;
import dto.ProductDTO;
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
            case "CreateProduct" -> {
                return createProduct(req, resp);
            }
            case "ListProducts" -> {
                return listProducts(req, resp);
            }
            case "UpdateProduct" -> {
                return updateProduct(req, resp);
            }
            case "NewProduct" -> {
                return newProduct();
            }
            case "EditProduct" -> {
                return editProduct(req, resp);
            }
            case "DeleteProduct" -> {
                return deleteProduct(req, resp);
            }
            default -> {
                logger.error("Error: action not found");
                req.setAttribute(ERROR, "Action not found");
            }

        }
        return FORWARD_PAGES_NOT_FOUND_JSP;
    }


    public String createProduct(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET creating product");
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
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + saved.getId();
    }

    public String editProduct(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET editing product");
        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        Product product = controller.findById(Long.parseLong(req.getParameter(ID)));
        req.setAttribute(PRODUCT, new ProductDTO(product));
        return FORWARD_PAGES_PRODUCT_FORM_UPDATE_PRODUCT_JSP;
    }

    private String listProducts(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET listing products by filter");

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
            } else {
                products = controller.findAllByDescription(value);
            }

            req.setAttribute(PRODUCTS, products);
        } else {
            products = controller.findAll();
            req.setAttribute(PRODUCTS, products);
        }
        return FORWARD_PAGES_PRODUCT_LIST_PRODUCTS_JSP;
    }

    private String newProduct() {
        logger.info("doGET redirecting to form createProduct");
        return FORWARD_PAGES_PRODUCT_FORM_CREATE_PRODUCT_JSP;
    }

    private String updateProduct(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET updating product");
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
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS_BY_ID + product.getId();
    }

    private String deleteProduct(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("doGET deleting product");

        if (!this.validate(req, resp)) {
            return FORWARD_PAGES_NOT_FOUND_JSP;
        }

        controller.delete(Long.parseLong(req.getParameter(ID)));
        return REDIRECT_PRODUCT_ACTION_LIST_PRODUCTS;
    }
}
