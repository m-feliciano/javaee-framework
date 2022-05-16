package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Setter(value = AccessLevel.NONE)
    List<Product> products = new ArrayList<>();
    private Integer id;
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public Category(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public void addProduct(Product prod) {
        products.add(prod);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" - ")
                .append(name).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (getClass() != obj.getClass()))
            return false;
        Category other = (Category) obj;
        return Objects.equals(id, other.id);
    }

}
