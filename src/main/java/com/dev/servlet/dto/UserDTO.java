package com.dev.servlet.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.Perfil;

public class UserDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String login;
	private String password;
	private Set<Perfil> perfis;
	private String imgUrl;

	public UserDTO() {
	}

	public UserDTO(User user) {
		this.setId(user.getId());
		this.setImgUrl(user.getImgUrl());
		this.setPerfis(user.getPerfis());
		this.setLogin(user.getLogin());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(Set<Perfil> perfis) {
		this.perfis = perfis;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
