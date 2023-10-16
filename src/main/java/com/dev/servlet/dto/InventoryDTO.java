package com.dev.servlet.dto;

import java.io.Serial;
import java.io.Serializable;

import com.dev.servlet.domain.Inventory;

public class InventoryDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer quantity;
	private String description;
	private ProductDTO productDto;

	public InventoryDTO() {
	}

	public InventoryDTO(Inventory inventory) {
		this.id = inventory.getId();
		this.quantity = inventory.getQuantity();
		this.description = inventory.getDescription();
		this.productDto = new ProductDTO(inventory.getProduct());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProductDTO getProductDto() {
		return productDto;
	}

	public void setProductDto(ProductDTO productDto) {
		this.productDto = productDto;
	}

}
