package com.dev.servlet.domain;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "tb_user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imgUrl;

    @ElementCollection(fetch = FetchType.EAGER) // Eager to load all roles
    @CollectionTable(name = "user_perfis", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "perfil_id")
    private List<Long> perfis;

    @Transient
    private String token;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
        this.perfis = new ArrayList<>();
    }

    public User(Long id) {
        this.id = id;
    }

    public void addPerfil(Long perfil) {
        if (this.perfis == null) {
            this.perfis = new ArrayList<>();
        }
        this.perfis.add(perfil);
    }

    public List<Long> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<Long> perfis) {
        this.perfis = perfis;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String newToken) {
        this.token = newToken;
    }
}
