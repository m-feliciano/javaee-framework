package dto;

import domain.Category;
import domain.Product;
import lombok.Data;
import utils.CurrencyFormatter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String description;
    private String url;
    private LocalDate registerDate;
    private String price;
    private Category category;
    private String status;

    /**
     * Instantiates a new Product dTO.
     *
     * @param product the product
     */
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.registerDate = product.getRegisterDate();
        this.price = CurrencyFormatter.bigDecimalToString(product.getPrice());
        this.url = product.getUrl();
        this.category = product.getCategory();
        this.status = product.getStatus();
    }

}
