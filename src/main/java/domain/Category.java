package domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_category")
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Setter(value = AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Transient
	@Setter(value = AccessLevel.NONE)
	@ManyToMany(mappedBy = "categories")
	private List<Product> products = new ArrayList<>();

	public Category(String name) {
		this.name = name;
	}

	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public void addProduct(Product prod) {
		if (prod == null) {
			throw new IllegalArgumentException("Cannot add an nullable product.");
		}
		products.add(prod);
	}

	public List<Product> getProducts() {
		return products;
	}

	@Override
	public String toString() {
		return "Category{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
