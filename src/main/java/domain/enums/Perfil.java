package domain.enums;

import lombok.Getter;

@Getter
public enum Perfil {

    ADMIN(1, "ROLE_ADMIN"),
    CLIENT(2, "ROLE_CLIENT"),
    MODERATOR(2, "ROLE_MODERATOR");

    private final int cod;

    private final String description;

    Perfil(int cod, String descricao) {
        this.cod = cod;
        this.description = descricao;
    }

    /**
     * Gets cod.
     *
     * @return the enum value
     */

    public static Perfil toEnum(Integer cod) {
        if (cod == null) {
            return null;
        }

        for (Perfil p : Perfil.values()) {
            if (cod.equals(p.getCod())) {
                return p;
            }
        }
        throw new IllegalAccessError("Invalid Id: " + cod);
    }

}
