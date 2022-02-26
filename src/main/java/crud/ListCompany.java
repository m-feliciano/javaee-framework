package crud;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import services.Validate;
import servlets.entities.Company;

public class ListCompany {

	public void doList(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single company");
		
		RequestDispatcher rd = req.getRequestDispatcher("/companyNotFound.jsp");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Storage storage = new Storage();
			Company company = storage.findById(id);
			if (company != null) {
				System.out.println(company);
				req.setAttribute("company", company);
				req.setAttribute("today", new Date());
				rd = req.getRequestDispatcher("/formUpdateCompany.jsp");
			}
		}
		try {
			rd.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
