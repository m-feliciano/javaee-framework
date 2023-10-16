package com.dev.servlet.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dev.servlet.dto.ProductDTO;
import com.dev.servlet.utils.CurrencyFormatter;

@Entity
@Table(name = "tb_product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "url_img", columnDefinition = "TEXT")
	private String url;

	@Column(name = "created_at")
	private LocalDate registerDate;

	@Column(name = "price")
	private BigDecimal price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "status")
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	public Product() {
	}

	public Product(String name, String description, String url, LocalDate registerDate, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.url = url;
		this.registerDate = registerDate;
		this.price = price;
	}

	public Product(Long id, String name, String description, String url, LocalDate registerDate, BigDecimal price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
		this.registerDate = registerDate;
		this.price = price;
	}

	public Product(ProductDTO dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.description = dto.getDescription();
		this.registerDate = dto.getRegisterDate();
		this.price = CurrencyFormatter.stringToBigDecimal(dto.getPrice());
		this.url = dto.getUrl();
		this.status = dto.getStatus();
	}

	public void setCategory(Category category) {
		this.category = category;
		category.getProducts().add(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDate getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDate registerDate) {
		this.registerDate = registerDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Category getCategory() {
		return category;
	}

}
