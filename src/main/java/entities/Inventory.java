package entities;

import lombok.Data;

@Data
public class Inventory {

	private Integer id;
	private Integer productId;
	private Integer categoryId;
	private Integer quantity;
	private String description;

	public Inventory(Integer productId, Integer categoryId, Integer quantity, String description) {
		super();
		this.productId = productId;
		this.categoryId = categoryId;
		this.quantity = quantity;
		this.description = description;
	}

}
