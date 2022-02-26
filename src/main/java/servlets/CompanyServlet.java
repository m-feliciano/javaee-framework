package servlets;

import java.io.IOException;

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
		String path = null;

		/* MAIN CRUD */
		if (param.equals("list")) {
			ListCompany listCompany = new ListCompany();
			path = listCompany.doList(req, resp);
		} else if (param.equals("listAll")) {
			ListCompanies listCompanies = new ListCompanies();
			path = listCompanies.doList(req, resp);
		} else if (param.equals("create")) {
			CreateCompany createCompany = new CreateCompany();
			path = createCompany.doCreate(req, resp);
		} else if (param.equals("delete")) {
			DeleteCompany deleteCompany = new DeleteCompany();
			path = deleteCompany.doDelete(req, resp);
		} else if (param.equals("update")) {
			UpdateCompany updateCompany = new UpdateCompany();
			path = updateCompany.doUpdate(req, resp);
		} else if (param.equals("new")) {
			try {
				req.getRequestDispatcher("/WEB-INF/view/formCreateCompany.jsp").forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		}

		String[] array = path.split(":");

		if (array[0].equals("forward")) {
			try {
				req.getRequestDispatcher("/WEB-INF/view/" + array[1]).forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		} else {
			try {
				resp.sendRedirect(array[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
