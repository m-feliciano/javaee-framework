package crud;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.Company;
import infra.CompanyDB;
import services.DateFormatter;

public class CreateCompany implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new company");
		String name = req.getParameter("name");
		String releaseDate = req.getParameter("date");
		Date date = DateFormatter.From(releaseDate);
		Company company = new Company(name, date);
		CompanyDB storage = new CompanyDB();
		storage.add(company);

		return "redirect:company?action=ListCompanies";

	}

}
