package com.dev.servlet.domain.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_inventory")
@Where(clause = "status = 'A'")
public class Inventory {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;

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

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }

    public Inventory(Product product, Integer quantity, String description) {
        this.product = product;
        this.quantity = quantity;
        this.description = description;
    }

    public InventoryBuilder toBuilder() {
        return Inventory.builder()
                .id(this.id)
                .quantity(this.quantity)
                .description(this.description)
                .status(this.status)
                .product(this.product)
                .user(this.user);
    }
}
