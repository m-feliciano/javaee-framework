package dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryDTO implements Serializable{

	private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer productId;
    private Integer categoryId;
    private Integer quantity;
    private String productName;
    private BigDecimal productPrice;
    private String categoryName;
    private String description;

}
