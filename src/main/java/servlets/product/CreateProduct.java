package servlets.product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.ProductController;
import domain.Product;
import servlets.Action;
import utils.CurrencyFormatter;
import utils.JPAUtil;

public class CreateProduct implements Action {

	private final EntityManager em = JPAUtil.getEntityManager();
	private final ProductController productController = new ProductController(em);

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST registering new product");
		String name = req.getParameter("name");
		String descriprion = req.getParameter("description");
		String priceString = req.getParameter("price");
		String url = req.getParameter("url");
		
		 LocalDate date = LocalDate.now();
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		 String text = date.format(formatter);
		 LocalDate parsedDate = LocalDate.parse(text, formatter);
		
		Product product = new Product(name, descriprion, url, parsedDate, CurrencyFormatter.stringToBigDecimal(priceString));
		productController.save(product);

		return "redirect:product?action=ListProducts";
	}

}
