package servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import servlets.entities.Company;

public class Storage {

	private static List<Company> companies = new ArrayList<>();

	static { // mock
		Company c1 = new Company("Twitter");
		Company c2 = new Company("Extra");
		Company c3 = new Company("Casa do Código");

		companies.addAll(Arrays.asList(c1, c2, c3));
	}

	public void add(Company company) {
		if (company.getName() != null) {
			companies.add(company);
		}
	}

	public List<Company> getCompanies() {
		return companies;
	}

}
