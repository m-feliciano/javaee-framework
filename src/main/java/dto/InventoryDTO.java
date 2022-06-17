package dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InventoryDTO {

    private Integer id;
    private Integer productId;
    private Integer categoryId;
    private Integer quantity;
    private String productName;
    private BigDecimal productPrice;
    private String categoryName;
    private String description;

}
