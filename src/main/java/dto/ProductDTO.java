package dto;

import java.text.SimpleDateFormat;

import entities.Product;
import lombok.Data;
import utils.CurrencyFormatter;

@Data
public class ProductDTO {
	private String id;
	private String name;
	private String description;

	private String url;
	private String registerDate;
	private String price;

	public ProductDTO(Product product) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.id = product.getId().toString();
		this.name = product.getName();
		this.description = product.getDescription();
		this.registerDate = sdf.format(product.getRegisterDate());
		this.price = CurrencyFormatter.bigDecimalToString(product.getPrice());
		this.url = product.getUrl();
	}

}
