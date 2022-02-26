package infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.User;

public class UserDB {

	private static List<User> users = new ArrayList<>();

	static {
		User u1 = new User("ana", "123");
		User u2 = new User("bia", "123");
		users.addAll(Arrays.asList(u1, u2));
	}

	public User findByEmail(String email) {
		for (User user : users) {
			if (user.getLogin().equals(email)) {
				return user;
			}
		}
		return null;
	}

}
