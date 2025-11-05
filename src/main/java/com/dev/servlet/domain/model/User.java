package com.dev.servlet.domain.model;

import com.dev.servlet.domain.model.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@Table(name = "tb_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString(exclude = "credentials")
public class User {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "short-uuid")
    @GenericGenerator(name = "short-uuid", strategy = "com.dev.servlet.core.util.ShortUuidGenerator")
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
    private List<Long> perfis;

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

    public void addPerfil(Long perfil) {
        if (this.perfis == null) {
            this.perfis = new ArrayList<>();
        }
        this.perfis.add(perfil);
    }

    public boolean hasRole(RoleType role) {
        if (this.perfis == null) {
            return false;
        }
        for (Long perfil : this.perfis) {
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
