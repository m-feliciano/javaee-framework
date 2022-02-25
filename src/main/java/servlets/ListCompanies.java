package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import servlets.entities.Company;

@WebServlet("/listCompanies")
public class ListCompanies extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing companies");
		Storage storage = new Storage();
		List<Company> companies = storage.getCompanies();
		req.setAttribute("companies", companies);
		RequestDispatcher rd = req.getRequestDispatcher("/listCompanies.jsp");

		try {
			rd.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
