package crud;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.CompanyDB;
import services.DateFormatter;
import services.Validate;

public class UpdateCompany implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing company");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			String name = req.getParameter("name");
			String releaseDate = req.getParameter("date");
			Date date = DateFormatter.From(releaseDate);
			CompanyDB storage = new CompanyDB();
			storage.findById(id).setName(name);
			storage.findById(id).setReleaseDate(date);
			req.setAttribute("company", storage);
		}
		return "redirect:company?action=listAll";
	}

}
