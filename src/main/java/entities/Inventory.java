package entities;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Inventory implements Serializable{
	private static final long serialVersionUID = 1L;

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
