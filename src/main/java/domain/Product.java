package domain;

import dto.ProductDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import utils.CurrencyFormatter;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static servlets.base.Base.PRODUCT_ID;
import static servlets.product.ProductServlet.USER_LOGGED;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@ToString
@Table(name = "tb_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "url_img", columnDefinition = "TEXT")
    private String url;
    @Column(name = "created_at")
    private LocalDate registerDate;
    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

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
        this.status = dto.getStatus();
    }

    public void setCategory(Category category) {
        this.category = category;
        category.getProducts().add(this);
    }

    public static Product getProductFromRequest(HttpServletRequest req) {
        Product product = new Product();
        product.setId(Long.parseLong(req.getParameter(PRODUCT_ID)));
        product.setUser((User) req.getSession().getAttribute(USER_LOGGED));
        return product;
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
