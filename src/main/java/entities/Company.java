package entities;

import java.util.Date;

import infra.CompanyDB;

public class Company {

	private int id;
	private String name;
	private Date releaseDate;

	public Company(String name) {
		this.id = CompanyDB.getSize() + 1;
		this.name = name;
		this.releaseDate = new Date();
	}

	public Company(String name, Date date) {
		this.id = CompanyDB.getSize() + 1;
		this.name = name;
		this.releaseDate = date;
	}

	public Company(int id, String name) {
		this.id = id;
		this.name = name;
		this.releaseDate = new Date();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + ", releaseDate=" + releaseDate + "]";
	}

}
