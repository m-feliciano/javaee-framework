package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Product;

public class CategoryDAO {

	private final Connection conn;

	public CategoryDAO(Connection conn) {
		this.conn = conn;
	}

	public Category findById(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_CATEGORY_SELECT_BY_ID)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Category cat = null;
			while (rs.next()) {
				cat = new Category(rs.getInt("id"), rs.getString("name"));
			}
			return cat;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void save(Category cat) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_CATEGORY_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, cat.getName());
			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Successfully added category\nInserted ID: " + id);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void update(Category cat) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_CATEGORY_UPDATE)) {
			ps.setString(1, cat.getName());
			ps.setInt(2, cat.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void delete(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_CATEGORY_DELETE)) {
			ps.setInt(1, id);
			ps.executeUpdate();
			int affectedRows = ps.getUpdateCount();
			if (affectedRows > 0) {
				System.out.println("Successfully delete category\nAffected rows: " + affectedRows);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<Category> list() {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_CATEGORIES)) {
			ResultSet rs = ps.executeQuery();
			List<Category> categories = new ArrayList<>();
			Category cat;
			while (rs.next()) {
				cat = new Category();
				cat.setId(rs.getInt(1));
				cat.setName(rs.getString(2));
				categories.add(cat);
			}
			return categories;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<Category> listProductByCategory() {

		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCTS_BY_CATEGORY)) {
			ps.execute();
			ResultSet rs = ps.getResultSet();
			List<Category> items = new ArrayList<>();
			Category last = null;
			Product prod = null;
			while (rs.next()) {
				if (last == null || !(last.getName().equals(rs.getString(2)))) {
					Category cat = new Category();
					cat.setId(rs.getInt(1));
					cat.setName(rs.getString(2));
					last = cat;
					items.add(cat);
				}
				prod = new Product();
				prod.setId(rs.getInt(3));
				prod.setName(rs.getString(4));
				prod.setDescription(rs.getString(5));
				prod.setPrice(rs.getBigDecimal(6));
				prod.setRegisterDate(rs.getTimestamp(7));
				last.addProduct(prod);
			}
			return items;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
