package com.dev.servlet.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import com.dev.servlet.domain.Product;
import com.dev.servlet.utils.CurrencyFormatter;

public class ProductDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;
	private String url;
	private String status;
	private String price;
	private LocalDate registerDate;
	private CategoryDTO categoryDTO;

	/**
	 * Instantiates a new Product dTO.
	 *
	 * @param product
	 */
	public ProductDTO(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.registerDate = product.getRegisterDate();
		this.price = CurrencyFormatter.bigDecimalToString(product.getPrice());
		this.url = product.getUrl();
		this.categoryDTO = new CategoryDTO(product.getCategory());
		this.status = product.getStatus();
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public CategoryDTO getCategoryDTO() {
		return categoryDTO;
	}

	public void setCategoryDTO(CategoryDTO categoryDTO) {
		this.categoryDTO = categoryDTO;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
