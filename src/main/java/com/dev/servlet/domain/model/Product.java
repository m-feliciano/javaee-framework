package com.dev.servlet.domain.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "tb_product")
@ToString(exclude = {"user", "category"})
public class Product implements Entity<String> {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url_img")
    private String url;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "register_date")
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)

    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")

    private Category category;
    public Product(String id) {
        this.id = id;
    }

    public Product(String id, User user) {
        this.id = id;
        this.user = user;
    }

    public Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            category.addProduct(this);
        }
    }
}
