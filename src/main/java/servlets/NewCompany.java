package servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.DateFormatter;
import servlets.entities.Company;

@WebServlet("/newCompany")
public class NewCompany extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST registering new company");
		String companyName = req.getParameter("name");
		String releaseDate = req.getParameter("date");
		Date date = DateFormatter.From(releaseDate);
		Company company = new Company(companyName, date);
		Storage storage = new Storage();
		storage.add(company);

		RequestDispatcher rd = req.getRequestDispatcher("/newCompanyCreated.jsp");
		req.setAttribute("companyName", company.getName());

		try {
			rd.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
