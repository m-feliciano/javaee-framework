package entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

	private String login;
	private String password;

	public boolean equals(String login, String password) {
		return this.login.equals(login) && this.password.equals(password);
	}

}
