package domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tb_inventory")
public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Setter(value = AccessLevel.NONE)
	private Long id;

	@ManyToOne
	private Product product;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private BigDecimal price;

	public Inventory(Product product, Integer quantity, String description, BigDecimal price) {
		this.product = product;
		this.quantity = quantity;
		this.description = description;
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		Inventory inventory = (Inventory) o;
		return id != null && Objects.equals(id, inventory.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
