package com.dev.servlet.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

@Embeddable
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreType
public class Credentials {
    @ColumnTransformer(write = "LOWER(?)")
    @Column(name = "login", unique = true)
    private String login;
    @JsonIgnore
    @Column(name = "password")
    private String password;
}
