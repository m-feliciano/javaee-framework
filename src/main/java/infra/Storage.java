package infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import servlets.entities.Company;

public class Storage {

	private static List<Company> companies = new ArrayList<Company>();

	static { // mock
		Company c1 = new Company(1, "Twitter");
		Company c2 = new Company(2, "Extra");
		Company c3 = new Company(3, "Casa do Código");
		companies.addAll(Arrays.asList(c1, c2, c3));
	}

	public static int getSize() {
		return companies.size();
	}

	public void add(Company company) {
		if (company.getName() != null) {
			companies.add(company);
		}
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public boolean delete(int id) {
		for (int i = 0; i < companies.size(); i++) {
			if (companies.get(i).getId() == id) {
				companies.remove(i);
				return true;
			}
		}
		return false;
	}

	public Company findById(int id) {
		for (Company company : companies) {
			if (company.getId() == id) {
				return company;
			}
		}

//		for (int i = 0; i < companies.size(); i++) {
//			if (companies.get(i).getId() == id) {
//				return companies.get(i);
//			}
//		}
		return null;

	}

}
