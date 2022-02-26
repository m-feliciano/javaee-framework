package infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import entities.Company;

public class CompanyDB {

	private static List<Company> companies = new ArrayList<Company>();

	private static int length = 0;

	public static int getSize() {
		return length;
	}

	public static void setSize(int length) {
		CompanyDB.length = length;
	}

	static { // mock test
		Calendar cal = Calendar.getInstance();
		cal.set(2018, 0, 10); // Jan
		Company c1 = new Company(1, "Twitter");
		c1.setReleaseDate(cal.getTime());
		Company c2 = new Company(2, "Extra");
		cal.set(1950, 11, 25); // Dez
		Company c3 = new Company(3, "Casa do Código");
		c3.setReleaseDate(cal.getTime());
		companies.addAll(Arrays.asList(c1, c2, c3));
		length += 3;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public Company findById(int id) {
		for (Company company : companies) {
			if (company.getId() == id) {
				return company;
			}
		}
		return null;
	}

	public void add(Company company) {
		if (!company.getName().isEmpty()) {
			CompanyDB.setSize(length + 1);
			companies.add(company);
		}
	}

	public void delete(int id) {
		companies.remove(findById(id));
	}

}
