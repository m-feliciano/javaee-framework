package com.dev.servlet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_file_image")
@Where(clause = "status = 'A'")
public class FileImage {

    @Id
    private UUID id;

    @Column(length = 100, nullable = false, name = "file_name")
    private String fileName;

    @Column(length = 50, nullable = false, name = "file_type")
    private String fileType;

    @Column(
            name = "uri",
            columnDefinition = "TEXT",
            nullable = false
    )
    private String uri;

    @Column(nullable = false)
    @ColumnTransformer(write = "UPPER(?)")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    @JsonIgnore
    private String externalSource;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }
}