package crud;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import servlets.entities.Company;

public class ListCompanies {

	public void doList(HttpServletRequest req, HttpServletResponse resp) {

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
