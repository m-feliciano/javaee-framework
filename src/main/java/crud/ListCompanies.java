package crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import servlets.entities.Company;

public class ListCompanies {

	public String doList(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doGET listing companies");
		Storage storage = new Storage();
		List<Company> companies = storage.getCompanies();
		req.setAttribute("companies", companies);

		return "forward:listCompanies.jsp";
	}

}
