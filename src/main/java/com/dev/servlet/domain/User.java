package com.dev.servlet.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.dev.servlet.domain.enums.Perfil;

@Table(name = "tb_user")
@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login", unique = true)
	private String login;

	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "image_url", columnDefinition = "TEXT")
	private String imgUrl;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "user_perfis", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "perfis")
	private Set<Integer> perfis;

	@Transient
	private String token;

	public User() {
	}

	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public User(Long id, String login, Set<Integer> perfis) {
		this.id = id;
		this.login = login;
		this.perfis.addAll(perfis);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addPerfil(Perfil perfil) {
		perfis.add(perfil.cod);
	}

	public Set<Perfil> getPerfis() {
		return perfis.stream().map(Perfil::toEnum).collect(Collectors.toSet());
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

	public String getToken() {
		return token;
	}

	public void setToken(String newToken) {
		this.token = newToken;
	}
}
