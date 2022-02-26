package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.CreateCompany;
import crud.DeleteCompany;
import crud.ListCompanies;
import crud.ListCompany;
import crud.UpdateCompany;

@WebServlet("/company")
public class CompanyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp) {

		String param = req.getParameter("action");

		/* MAIN CRUD */

		if (param.equals("list")) {
			ListCompany listCompany = new ListCompany();
			listCompany.doList(req, resp);
		} else if (param.equals("listAll")) {
			ListCompanies listCompany = new ListCompanies();
			listCompany.doList(req, resp);
		} else if (param.equals("create")) {
			CreateCompany createCompany = new CreateCompany();
			createCompany.doCreate(req, resp);
		} else if (param.equals("delete")) {
			DeleteCompany deleteCompany = new DeleteCompany();
			deleteCompany.doDelete(req, resp);
		} else if (param.equals("update")) {
			UpdateCompany updateCompany = new UpdateCompany();
			updateCompany.doUpdate(req, resp);
		}

		/* HELPERS */

		else if (param.equals("new")) {
			try {
				RequestDispatcher rd = req.getRequestDispatcher("/formCreateCompany.jsp");
				rd.forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		}

	}

}
