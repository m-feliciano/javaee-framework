package entities;

import java.io.Serializable;
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
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String description;

	private String url;
	private Date registerDate;
	private BigDecimal price;

	public Product(String name, String description, String url, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.url = url;
		this.price = price;
	}

	public Product(Integer id, String name, String description, String url, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
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
			this.url = dto.getUrl();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", url=" + url
				+ ", registerDate=" + registerDate + ", price=" + price + "]";
	}

//	@Override
//	public String toString() {
//		return new StringBuilder().append(id).append(" - ").append(name).toString();
//	}

}
