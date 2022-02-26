package entities;

public class User {

	private String login;
	private String password;

	public User() {
		super();
	}

	public User(String login, String password) {
		super();
		this.login = login;
		this.password = password;
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

	public boolean isEqual(String login, String pass) {
		if (this.login.equals(login) && this.password.equals(pass)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "User [login=" + login + ", password=" + password + "]";
	}

}
