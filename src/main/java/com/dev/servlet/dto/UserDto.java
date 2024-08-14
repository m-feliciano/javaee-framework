package com.dev.servlet.dto;

import com.dev.servlet.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link User}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {
    private final Long id;

    private String login;
    private String password;
    private String status;
    private String imgUrl;
    private List<Long> perfis;
    private String token;


    public UserDto(Long id) {
        this.id = id;
    }

    public UserDto(Long id, String login, String status, String imgUrl, List<Long> perfis, String token) {
        this.id = id;
        this.login = login;
        this.status = status;
        this.imgUrl = imgUrl;
        this.perfis = perfis;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getStatus() {
        return status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<Long> getPerfis() {
        return perfis;
    }

    public String getToken() {
        return token;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setPerfis(List<Long> perfis) {
        this.perfis = perfis;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "login = " + login + ", " +
                "status = " + status + ", " +
                "imgUrl = " + imgUrl + ", " +
                "perfis = " + perfis + ", " +
                "token = " + token + ")";
    }
}