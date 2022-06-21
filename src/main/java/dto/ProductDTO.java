package dto;

import domain.Product;
import lombok.Data;
import utils.CurrencyFormatter;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;

	private String url;
	private LocalDate registerDate;
	private String price;

	public ProductDTO(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.registerDate = product.getRegisterDate();
		this.price = CurrencyFormatter.bigDecimalToString(product.getPrice());
		this.url = product.getUrl();
	}

}
