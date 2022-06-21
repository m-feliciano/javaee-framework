package domain.enums;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public enum Perfil {

	ADMIN(1, "ROLE_ADMIN"),
	CLIENT(2, "ROLE_CLIENT"),
	MODERATOR(2, "ROLE_MODERATOR");

	@Setter(value = AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final int cod;

	@Column(name = "description")
	private final String description;

	Perfil(int cod, String descricao) {
		this.cod = cod;
		this.description = descricao;
	}

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
