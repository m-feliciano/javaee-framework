package entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Inventory implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
    private Integer productId;
    private Integer categoryId;
    private Integer quantity;
    private String description;


    public Inventory(Integer productId, Integer categoryId, Integer quantity, String description) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", productId=" + productId +
                ", categoryId=" + categoryId +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                '}';
    }
}
