package crud;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import services.DateFormatter;
import servlets.entities.Company;

public class CreateCompany {

	public String doCreate(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doPOST registering new company");
		String name = req.getParameter("name");
		String releaseDate = req.getParameter("date");
		Date date = DateFormatter.From(releaseDate);
		Company company = new Company(name, date);
		Storage storage = new Storage();
		storage.add(company);

		return "redirect:company?action=listAll";

	}

}
