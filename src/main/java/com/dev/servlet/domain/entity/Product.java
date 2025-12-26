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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_product")
@ToString(exclude = {"category", "owner", "thumbnails"})
@Where(clause = "status = 'A'")
public class Product {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<FileImage> thumbnails;

    @Column(name = "register_date", updatable = false)
    private LocalDate registerDate;
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(UUID id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            category.addProduct(this);
        }
    }

    public void addThumbnail(FileImage file) {
        if (this.thumbnails == null) this.thumbnails = new LinkedList<>();
        this.thumbnails.add(file);
        file.setProduct(this);
    }

    @JsonIgnore
    public String getThumbnail() {
        return hasThumbnails() ? this.thumbnails.getFirst().getUri() : null;
    }

    public boolean hasThumbnails() {
        return thumbnails != null && !thumbnails.isEmpty();
    }

}
