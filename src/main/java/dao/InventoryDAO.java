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

import dto.InventoryDTO;
import entities.Inventory;
import infra.Query;
import infra.exceptions.CustomRuntimeException;

public class InventoryDAO {

	private final Connection conn;

	final Logger logger = LoggerFactory.getLogger(InventoryDAO.class);

	public InventoryDAO(Connection conn) {
		this.conn = conn;
	}

	public Inventory findById(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_SELECT_BY_ID)) {
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {

				Inventory inventory = null;
				while (rs.next()) {
					inventory = new Inventory();
					inventory.setId(rs.getInt("id"));
					inventory.setProductId(rs.getInt("product_id"));
					inventory.setCategoryId(rs.getInt("category_id"));
					inventory.setQuantity(rs.getInt("quantity"));
					inventory.setDescription(rs.getString("description"));
				}
				return inventory;
			} catch (Exception e) {
				throw new CustomRuntimeException(e.getMessage());
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public List<InventoryDTO> list() {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_SELECT_LIST_JOIN)) {
			ResultSet rs = ps.executeQuery();
			List<InventoryDTO> inventoriesVo = new ArrayList<>();
			InventoryDTO inventoryVo;
			while (rs.next()) {
				inventoryVo = getDTOFromResultSet(rs);
				inventoriesVo.add(inventoryVo);
			}
			return inventoriesVo;
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void save(Inventory inventory) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, inventory.getProductId());
			ps.setInt(2, inventory.getCategoryId());
			ps.setInt(3, inventory.getQuantity());
			ps.setString(4, inventory.getDescription());
			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					logger.info("Successfully added item: ID: " + id);
				}
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void update(Inventory inventory) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_UPDATE)) {
			ps.setInt(1, inventory.getProductId());
			ps.setInt(2, inventory.getCategoryId());
			ps.setInt(3, inventory.getQuantity());
			ps.setString(4, inventory.getDescription());
			ps.setInt(5, inventory.getId());

			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				logger.info("Successfully update item");
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public void delete(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_DELETE)) {

			ps.setInt(1, id);
			ps.executeUpdate();
			int affectedRows = ps.getUpdateCount();
			if (affectedRows > 0) {
				logger.info("Successfully delete item: " + id);
			}
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	public List<InventoryDTO> findByDescription(String description) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_SELECT_LIST_JOIN_BY_DESCRIPTION)) {
			ps.setString(1, "%" + description + "%");
			ResultSet rs = ps.executeQuery();
			List<InventoryDTO> inventoriesVo = new ArrayList<>();
			InventoryDTO inventoryVo;
			while (rs.next()) {
				inventoryVo = getDTOFromResultSet(rs);
				inventoriesVo.add(inventoryVo);
			}
			return inventoriesVo;
		} catch (SQLException e) {
			throw new CustomRuntimeException(e.getMessage());
		}
	}

	private InventoryDTO getDTOFromResultSet(ResultSet rs) {
		InventoryDTO vo = new InventoryDTO();
		try {
			vo.setId(rs.getInt("id"));
			vo.setProductId(rs.getInt("p_id"));
			vo.setProductName(rs.getString("p_name"));
			vo.setProductPrice(rs.getBigDecimal("p_price"));
			vo.setCategoryId(rs.getInt("c_id"));
			vo.setCategoryName(rs.getString("c_name"));
			vo.setQuantity(rs.getInt("quantity"));
			vo.setDescription(rs.getString("description"));
		} catch (Exception e) {
			throw new CustomRuntimeException("cannot instantiate itens from current resultset.");
		}
		return vo;
	}
}
