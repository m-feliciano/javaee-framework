package entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Inventory {

	private Integer id;
	private Integer productId;
	private Integer categoryId;
	private String productUrl;
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
