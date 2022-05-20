package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dto.InventoryDTO;
import entities.Inventory;
import infra.Query;

public class InventoryDAO {

	private final Connection conn;

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
                throw new RuntimeException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

	public List<InventoryDTO> list() {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_SELECT_LIST_JOIN)) {
			ResultSet rs = ps.executeQuery();
			List<InventoryDTO> inventoriesVo = new ArrayList<>();
			InventoryDTO inventoryVo;
			while (rs.next()) {
				inventoryVo = instantiateVO(rs);
				inventoriesVo.add(inventoryVo);
			}
			return inventoriesVo;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
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
					System.out.println("Successfully added constraint\nInserted ID: " + id);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
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
				System.out.println("Successfully update constraint");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void delete(int id) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_DELETE)) {

			ps.setInt(1, id);
			ps.executeUpdate();
			int affectedRows = ps.getUpdateCount();
			if (affectedRows > 0) {
				System.out.println("Successfully delete constraint\nAffected rows: " + affectedRows);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<InventoryDTO> findByDescription(String description) {
		try (PreparedStatement ps = conn.prepareStatement(Query.SQL_INVENTORY_SELECT_LIST_JOIN_BY_DESCRIPTION)) {
			ps.setString(1, "%" + description + "%");
			ResultSet rs = ps.executeQuery();
			List<InventoryDTO> inventoriesVo = new ArrayList<>();
			InventoryDTO inventoryVo;
			while (rs.next()) {
				inventoryVo = instantiateVO(rs);
				inventoriesVo.add(inventoryVo);
			}
			return inventoriesVo;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private InventoryDTO instantiateVO(ResultSet rs) {
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
			throw new RuntimeException("cannot instantiate itens from current resultset.");
		}
		return vo;
	}
}
