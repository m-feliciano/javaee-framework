package crud;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.CompanyDB;
import services.Validate;

public class DeleteCompany implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST deleting company");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			CompanyDB storage = new CompanyDB();
			storage.delete(id);
		}
		return "redirect:company?action=listAll";
	}

}
