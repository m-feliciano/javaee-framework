package infra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import entities.User;

public class UserDB {

	private static List<User> users = new ArrayList<>();

	static {
		User u1 = new User("ana", "123");
		User u2 = new User("bia", "123");
		users.addAll(Arrays.asList(u1, u2));
	}

	public User findByEmail(String email) {
		Optional<User> optional = users.stream().filter(user -> user.getLogin().equals(email)).findAny();
		return optional.isPresent() ? optional.get() : null;
	}

}
