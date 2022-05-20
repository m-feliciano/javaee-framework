package dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryDTO {

    private Integer id;
    private Integer productId;
    private Integer categoryId;
    private Integer quantity;
    private String productName;
    private BigDecimal productPrice;
    private String categoryName;
    private String description;

    @Override
	public String toString() {
		return "InventoryVO [id=" + id
				+ ", productId=" + productId
				+ ", categoryId=" + categoryId
				+ ", quantity=" + quantity
				+ ", productName=" + productName
				+ ", productPrice=" + productPrice
				+ ", categoryName=" + categoryName
				+ ", description=" + description + "]";
	}


}
