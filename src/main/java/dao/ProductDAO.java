package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Product;
import infra.Query;

public class ProductDAO {

	private final Connection conn;

	public ProductDAO(Connection conn) {
		this.conn = conn;
	}

	public Product findById(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCT_SELECT_BY_ID)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()){
				return instantiateProduct(rs);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return null;
	}

	public List<Product> list() {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCTS_SELECT)) {
			ResultSet rs = ps.executeQuery();
			List<Product> products = new ArrayList<>();
			while (rs.next()) {
				products.add(instantiateProduct(rs));
			}
			return products;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void save(Product prod) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCT_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, prod.getName());
			ps.setString(2, prod.getDescription());
			ps.setBigDecimal(3, prod.getPrice());

			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Successufully added product\nInserted ID: " + id);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void update(Product prod) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCTS_UPDATE)) {
			ps.setString(1, prod.getName());
			ps.setString(2, prod.getDescription());
			ps.setBigDecimal(3, prod.getPrice());
			ps.setInt(4, prod.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void delete(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCT_DELETE)) {
			ps.setInt(1, id);
			ps.executeUpdate();
			int affectedRows = ps.getUpdateCount();
			if (affectedRows > 0) {
				System.out.println("Successfully delete product\nAffected rows: " + affectedRows);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<Product> getProductsByCategoryName(String name) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_PRODUCTS_BY_CATEGORY_NAME)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			List<Product> products = new ArrayList<>();
			while (rs.next()) {
				products.add(instantiateProduct(rs));
			}
			return products;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	// bean
	private Product instantiateProduct(ResultSet rs) throws SQLException {
		Product prod = new Product();
		prod.setId(rs.getInt(1));
		prod.setName(rs.getString(2));
		prod.setDescription(rs.getString(3));
		prod.setPrice(rs.getBigDecimal(4));
		prod.setRegisterDate(rs.getTimestamp(5));
		return prod;
	}

}
