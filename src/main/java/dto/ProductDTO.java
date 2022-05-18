package dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CurrencyFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private String registerDate;
	private String price;

	public ProductDTO(String name, String description, String price) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public ProductDTO(String name, String description, String registerDate, String price) {
		super();
		this.name = name;
		this.description = description;
		this.registerDate = registerDate;
		this.price = price;
	}

	public ProductDTO(Product product) {	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");		
		this.id = product.getId().toString();
		this.name = product.getName();
		this.description =  product.getDescription();
		this.registerDate = sdf.format(product.getRegisterDate());
		this.price = CurrencyFormatter.bigDecimalToString(product.getPrice());
	}
	

}
