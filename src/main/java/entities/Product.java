package entities;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CurrencyFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	public Product(Integer id, String name, String description, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}

}
