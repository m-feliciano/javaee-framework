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
import crud.Login;
import crud.LoginForm;
import crud.NewCompany;
import crud.UpdateCompany;

@WebServlet("/company")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp) {

		String param = req.getParameter("action");
		String path = null;

		/* MAIN CRUD */
		if (param.equals("list")) {
			ListCompany listCompany = new ListCompany();
			path = listCompany.doService(req, resp);
		} else if (param.equals("listAll")) {
			ListCompanies listCompanies = new ListCompanies();
			path = listCompanies.doService(req, resp);
		} else if (param.equals("create")) {
			CreateCompany createCompany = new CreateCompany();
			path = createCompany.doService(req, resp);
		} else if (param.equals("delete")) {
			DeleteCompany deleteCompany = new DeleteCompany();
			path = deleteCompany.doService(req, resp);
		} else if (param.equals("update")) {
			UpdateCompany updateCompany = new UpdateCompany();
			path = updateCompany.doService(req, resp);
		} else if (param.equals("new")) {
			NewCompany newCompany = new NewCompany();
			path = newCompany.doService(req, resp);
		} else if (param.equals("loginForm")) {
			LoginForm login = new LoginForm();
			path = login.doService(req, resp);
		} else if (param.equals("login")) {
			System.out.println("login");
			Login login = new Login();
			path = login.doService(req, resp);
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
