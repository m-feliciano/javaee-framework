package servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import services.Validate;

@WebServlet("/deleteCompany")
public class DeleteCompany extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST deleting company");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Storage storage = new Storage();
			storage.delete(id);
		}
		try {
			resp.sendRedirect("listCompanies");
		} catch (IOException e) {
			throw new Error("Cannot redirect after delete: " + e.getMessage());
		}
	}

}
