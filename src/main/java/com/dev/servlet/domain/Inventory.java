package com.dev.servlet.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "tb_inventory")
@Entity
public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "description")
	private String description;

	@JoinColumn(name = "product")
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Column(name = "status")
	private String status;

	public Inventory() {
	}

	public Inventory(Long id) {
		this.id = id;
	}

	public Inventory(Product product, Integer quantity, String description) {
		this.product = product;
		this.quantity = quantity;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

}
