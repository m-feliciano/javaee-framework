package entities;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dto.ProductDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.CurrencyFormatter;

@Data
@NoArgsConstructor
public class Product {

	private Integer id;
	private String name;
	private String description;
	private Date registerDate;
	private BigDecimal price;

	public Product(String name, String description, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public Product(String name, String description, Date registerDate, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.registerDate = registerDate;
		this.price = price;
	}

	public Product(ProductDTO dto) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			this.id = Integer.parseInt(dto.getId());
			this.name = dto.getName();
			this.description = dto.getDescription();
			this.registerDate = sdf.parse(dto.getRegisterDate());
			this.price = CurrencyFormatter.stringToBigDecimal(dto.getPrice());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return new StringBuilder().append(id).append(" - ").append(name).toString();
	}

}
