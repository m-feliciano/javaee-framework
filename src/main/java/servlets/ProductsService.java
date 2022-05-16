package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import dao.ProductDAO;
import entities.Product;
import infra.ConnectionFactory;

@WebServlet("/Products")
public class ProductsService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection conn;
	@Override

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		conn = new ConnectionFactory().getConnection();
		ProductDAO dao = new ProductDAO(conn);
		List<Product> productsList = dao.list();

		String header = request.getHeader("Accept");
		String format = null;

		if (header == null) {
			response.getWriter().print("{ 'message': 'no content' }");
		} else {
			if (header.contains("xml")) {
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("product", Product.class);
				xstream.aliasField("release", Product.class, "registerDate");
				format = xstream.toXML(productsList);
				response.setContentType("application/xml");
			} else {
				Gson gson = new Gson();
				format = gson.toJson(productsList);
				response.setContentType("application/json");
			}
			response.getWriter().print(format);
		}

	}

}