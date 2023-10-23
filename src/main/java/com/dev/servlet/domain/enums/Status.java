package com.dev.servlet.domain.enums;

import java.util.Arrays;

public enum Status {
	ACTIVE(1, "A"), DELETED(2, "X");

	private final int cod;
	private final String description;

	Status(int cod, String description) {
		this.cod = cod;
		this.description = description;
	}

	public int getCod() {
		return cod;
	}

	public String getDescription() {
		return description;
	}

	public static Status getByCode(int cod) {
		return Arrays.stream(Status.values())
				.filter(id -> id != null && id.cod == cod)
				.findFirst()
				.orElse(null);
	}
}
