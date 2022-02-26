package crud;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import infra.Storage;
import services.DateFormatter;
import services.Validate;

public class UpdateCompany {

	public String doUpdate(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("doPOST editing company");
		if (Validate.isValid(req, "id")) {
			int id = Integer.parseInt(req.getParameter("id"));
			String name = req.getParameter("name");
			String releaseDate = req.getParameter("date");
			Date date = DateFormatter.From(releaseDate);
			Storage storage = new Storage();
			storage.findById(id).setName(name);
			storage.findById(id).setReleaseDate(date);
			req.setAttribute("company", storage);
		}
		return "redirect:company?action=listAll";
	}

}
