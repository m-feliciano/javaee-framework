package com.dev.servlet.domain.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@javax.persistence.Table(name = "tb_inventory")
public class Inventory implements Entity<String> {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "description")
    private String description;
    @Column(name = "status", nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Inventory(String id) {
        this.id = id;
    }

    public Inventory(Product product, Integer quantity, String description) {
        this.product = product;
        this.quantity = quantity;
        this.description = description;
    }
}
