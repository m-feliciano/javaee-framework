package servlets.entities;

import java.util.Date;
import java.util.UUID;

public class Company {

	private String id;
	private String name;
	private Date releaseDate;

	public Company(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.releaseDate = new Date();
	}
	
	public Company(String name, Date date) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.releaseDate = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
