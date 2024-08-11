package com.dev.servlet.domain.enums;

public enum PerfilEnum {

    ADMIN(1L, "ADMIN"),
    DEFAULT(2L, "USER"),
    MODERATOR(3L, "MODERATOR"),
    VISITOR(4L, "GUEST");

    public final Long cod;
    public final String description;

    PerfilEnum(Long cod, String descricao) {
        this.cod = cod;
        this.description = descricao;
    }

    /**
     * Gets cod.
     *
     * @return the enum value
     */
    public static PerfilEnum toEnum(Long cod) {
        if (cod == null) {
            return null;
        }

        for (PerfilEnum p : PerfilEnum.values()) {
            if (cod.equals(p.cod))
                return p;
        }
        throw new IllegalArgumentException("Invalid Id: " + cod);
    }
}
