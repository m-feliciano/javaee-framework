package com.dev.servlet.domain.entity;

import com.dev.servlet.domain.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
@ToString(exclude = {"credentials", "images"})
@Where(clause = "status IN ('A', 'P')")
public class User {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "short-uuid")
    @GenericGenerator(name = "short-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Embedded
    private Credentials credentials;

    @Setter
    @Column(name = "status")
    @ColumnTransformer(write = "UPPER(?)")
    private String status;

    @Column(name = "config")
    private String config;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_perfis", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "perfil_id")
    private List<Integer> perfis;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<FileImage> images;

    @Transient
    @JsonIgnore
    private String token;
    @Transient
    @JsonIgnore
    private String refreshToken;

    public User(String id) {
        this.id = id;
    }

    public User(String login, String password) {
        this.setLogin(login);
        this.setPassword(password);
    }

    public User(String login, String password, Status status) {
        this.setLogin(login);
        this.setPassword(password);
        this.status = status.getValue();
    }

    @JsonIgnore
    public String getLogin() {
        return credentials != null ? credentials.getLogin() : null;
    }

    public void setLogin(String login) {
        this.credentials = Credentials.builder()
                .login(login)
                .password(this.getPassword())
                .build();
    }

    @JsonIgnore
    public String getPassword() {
        return credentials != null ? credentials.getPassword() : null;
    }

    public void setPassword(String password) {
        this.credentials = Credentials.builder()
                .login(this.getLogin())
                .password(password)
                .build();
    }

    @JsonIgnore
    public FileImage getProfileImage() {
        return hasProfileImage() ? images.getFirst() : null;
    }

    private boolean hasProfileImage() {
        return images != null && !images.isEmpty();
    }
}
