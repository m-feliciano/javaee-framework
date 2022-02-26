package crud;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.Company;
import infra.CompanyDB;
import services.Validate;

public class ListCompany implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doGET listing single company");

		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			CompanyDB storage = new CompanyDB();
			Company company = storage.findById(id);
			if (company != null) {
				System.out.println(company);
				req.setAttribute("company", company);
				req.setAttribute("today", new Date());
				return "forward:formUpdateCompany.jsp";
			}
		}
		return "forward:companyNotFound.jsp";
	}

}
