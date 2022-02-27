package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import entities.Company;
import infra.CompanyDB;

@WebServlet("/Companies")
public class CompaniesService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<Company> companies = new CompanyDB().getCompanies();

		String header = request.getHeader("Accept");
		String format = null;

		if (header == null) {
			response.getWriter().print("{ 'message': 'no content' }");
		} else {
			if (header.contains("xml")) {
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("company", Company.class);
				xstream.aliasField("release", Company.class, "releaseDate");
				format = xstream.toXML(companies);
				response.setContentType("application/xml");
			} else {
				Gson gson = new Gson();
				format = gson.toJson(companies);
				response.setContentType("application/json");
			}
			response.getWriter().print(format);
		}

	}

}