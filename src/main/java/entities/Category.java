package entities;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Category {

	@Setter(value = AccessLevel.NONE)
	List<Product> products = new ArrayList<>();
	private Integer id;
	private String name;

	public Category(String name) {
		super();
		this.name = name;
	}

	public Category(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public void addProduct(Product prod) {
		products.add(prod);
	}

	@Override
	public String toString() {
		return new StringBuilder().append(id).append(" - ").append(name).toString();
	}

}
