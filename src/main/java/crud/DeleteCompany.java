package crud;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import services.Validate;

public class DeleteCompany {

	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST deleting company");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			Storage storage = new Storage();
			storage.delete(id);
		}
		try {
			resp.sendRedirect("company?action=listAll");
		} catch (IOException e) {
			throw new Error("Cannot redirect after delete: " + e.getMessage());
		}
	}

}
