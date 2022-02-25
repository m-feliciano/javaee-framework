package infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import servlets.entities.Company;

public class Storage {

	private static List<Company> companies = new ArrayList<Company>();
	private static int length = 0;

	public static int getSize() {
		return length;
	}

	public static void setSize(int length) {
		Storage.length = length;
	}

	static { // mock
		Company c1 = new Company(1, "Twitter");
		Company c2 = new Company(2, "Extra");
		Company c3 = new Company(3, "Casa do Código");
		companies.addAll(Arrays.asList(c1, c2, c3));
		length += 3;
	}

	public void add(Company company) {
		if (!company.getName().isEmpty()) {
			Storage.setSize(length + 1);
			companies.add(company);
		}
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void delete(int id) {
		companies.remove(findById(id));
	}

	public Company findById(int id) {
		for (Company company : companies) {
			if (company.getId() == id) {
				return company;
			}
		}
		return null;

	}

}
