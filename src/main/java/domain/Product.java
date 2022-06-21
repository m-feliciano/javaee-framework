package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.Hibernate;

import dto.ProductDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import utils.CurrencyFormatter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "tb_product")
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Setter(value = AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name", length = 100)
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "url_img")
	private String url;
	@Column(name = "created_at")
	private LocalDate registerDate;
	@Column(name = "price")
	private BigDecimal price;

	@Transient
	@Setter(value = AccessLevel.NONE)
	@JsonIgnore // deny serialization
	@ManyToMany
	@JoinTable(name = "PRODUCT_CATEGORY", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	@ToString.Exclude
	private List<Category> categories = new ArrayList<>();

	public Product(String name, String description, String url, LocalDate registerDate, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.url = url;
		this.registerDate = registerDate;
		this.price = price;
	}

	public Product(Long id, String name, String description, String url, LocalDate registerDate, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
		this.registerDate = registerDate;
		this.price = price;
	}

	public Product(ProductDTO dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.description = dto.getDescription();
		this.registerDate = dto.getRegisterDate();
		this.price = CurrencyFormatter.stringToBigDecimal(dto.getPrice());
		this.url = dto.getUrl();
	}

	public void addCategory(Category category) {
		if (category == null) {
			throw new IllegalArgumentException("Category must not be nullable");
		}
		this.categories.add(category);
	}

	public List<Category> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Product{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", url='" + url + '\''
				+ ", registerDate=" + registerDate + ", price=" + price + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		Product product = (Product) o;
		return id != null && Objects.equals(id, product.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
