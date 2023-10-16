package com.dev.servlet.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.dev.servlet.domain.Category;

public class CategoryDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private List<ProductDTO> productDTOlist;

	public CategoryDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();

		if (category.getProducts() != null) {
			this.productDTOlist = category.getProducts().stream()
					.map(ProductDTO::new)
					.toList();
		}
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

	public List<ProductDTO> getProductDTOlist() {
		return productDTOlist;
	}

	public void setProductDTOlist(List<ProductDTO> productDTOlist) {
		this.productDTOlist = productDTOlist;
	}

}
