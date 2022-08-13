package domain.enums;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
public enum Status {
    ACTIVE(1, "A"),
    DELETED(2, "X");

    private final int cod;
    private final String description;

    Status(int cod, String description) {
        this.cod = cod;
        this.description = description;
    }

    public static Status getByCode(int cod) {
        return Arrays.stream(Status.values()).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id: " + cod));
    }
}
