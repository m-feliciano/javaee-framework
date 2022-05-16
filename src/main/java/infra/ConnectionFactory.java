package infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import infra.exceptions.DbException;

public class ConnectionFactory {

	public Connection getConnection() {
		String url = "jdbc:postgresql://localhost:5432/teste_dm";
		String user = "postgres";
		String pass = "password";

		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException | ClassNotFoundException e) {
			throw new DbException(e.getMessage());
		}

	}
}
