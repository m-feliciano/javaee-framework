package entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String login;
	private String password;

	public boolean equals(String login, String password) {
		return this.login.equals(login) && this.password.equals(password);
	}

}
