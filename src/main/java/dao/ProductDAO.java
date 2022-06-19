package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Product;
import infra.Query;
import infra.exceptions.CustomRuntimeException;

public class ProductDAO {

	private final Connection conn;
	final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

	public ProductDAO(Connection conn) {
		this.conn = conn;
	}

	public Product findById(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCT_SELECT_BY_ID)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return instantiateProduct(rs);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(e.getMessage());
		}
		return null;
	}

	public List<Product> list() {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCTS_SELECT)) {
			ResultSet rs = ps.executeQuery();
			List<Product> products = new ArrayList<>();
			while (rs.next()) {
				products.add(instantiateProduct(rs));
			}
			return products;
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void save(Product prod) {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCT_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, prod.getName());
			ps.setString(2, prod.getDescription());
			ps.setBigDecimal(3, prod.getPrice());
			ps.setString(4, prod.getUrl());

			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					logger.info("Successufully added product: Id= " + id);
				}
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void update(Product prod) {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCTS_UPDATE)) {
			ps.setString(1, prod.getName());
			ps.setString(2, prod.getDescription());
			ps.setBigDecimal(3, prod.getPrice());
			ps.setString(4, prod.getUrl());
			ps.setInt(5, prod.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void delete(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCT_DELETE)) {
			ps.setInt(1, id);
			ps.executeUpdate();
			int affectedRows = ps.getUpdateCount();
			if (affectedRows > 0) {
				logger.info("Successfully delete product: Id= " + id);
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public List<Product> getProductsByCategory(String name) {
		try (PreparedStatement ps = conn.prepareStatement(Query.PRODUCTS_BY_CATEGORY)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			List<Product> products = new ArrayList<>();
			while (rs.next()) {
				products.add(instantiateProduct(rs));
			}
			return products;
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	// bean
	private Product instantiateProduct(ResultSet rs) throws SQLException {
		Product prod = new Product();
		prod.setId(rs.getInt("id"));
		prod.setName(rs.getString("name"));
		prod.setDescription(rs.getString("description"));
		prod.setPrice(rs.getBigDecimal("price"));
		prod.setRegisterDate(rs.getTimestamp("created_at"));
		prod.setUrl(rs.getString("url_img"));
		return prod;
	}

}
