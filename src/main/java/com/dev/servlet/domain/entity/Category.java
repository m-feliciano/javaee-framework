package com.dev.servlet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_category")
@ToString(of = {"id", "name"})
@Where(clause = "status = 'A'")
public class Category {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "name", nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String name;
    @Column(name = "status", nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String status;
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Category(UUID id) {
        this.id = id;
    }

    public Category(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(User user) {
        this.user = user;
    }

    public void addProduct(Product product) {
        if (products == null)
            products = new ArrayList<>();
        products.add(product);
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }
}
