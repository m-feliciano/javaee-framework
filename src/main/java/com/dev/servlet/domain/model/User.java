package com.dev.servlet.domain.model;

import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "tb_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString(exclude = "credentials")
public class User {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "short-uuid")
    @GenericGenerator(name = "short-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Embedded
    private Credentials credentials;

    @Column(name = "status")
    @ColumnTransformer(write = "UPPER(?)")
    private String status;

    @Column(name = "image_url")
    private String imgUrl;

    @Column(name = "config")
    private String config;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_perfis", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "perfil_id")
    private List<Integer> perfis;

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

    public void addPerfil(Integer perfil) {
        if (this.perfis == null) {
            this.perfis = new ArrayList<>();
        }
        this.perfis.add(perfil);
    }

    public boolean hasRole(RoleType role) {
        if (this.perfis == null) {
            return false;
        }
        for (Integer perfil : this.perfis) {
            if (RoleType.toEnum(perfil).equals(role)) {
                return true;
            }
        }
        return false;
    }

    public void setLogin(String login) {
        if (credentials == null) credentials = new Credentials();
        this.credentials.setLogin(login);
    }

    public void setPassword(String password) {
        if (credentials == null) credentials = new Credentials();
        this.credentials.setPassword(password);
    }

    @JsonIgnore
    public String getLogin() {
        return credentials != null ? credentials.getLogin() : null;
    }
    @JsonIgnore
    public String getPassword() {
        return credentials != null ? credentials.getPassword() : null;
    }
}
