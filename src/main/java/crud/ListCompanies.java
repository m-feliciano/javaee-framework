package crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.Company;
import infra.CompanyDB;

public class ListCompanies implements Action {

	@Override
	public String doService(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("doGET listing companies");
		CompanyDB storage = new CompanyDB();
		List<Company> companies = storage.getCompanies();
		req.setAttribute("companies", companies);

		return "forward:listCompanies.jsp";
	}

}
