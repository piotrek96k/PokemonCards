package com.pokemoncards.model.session;

public enum Expander {

	RARITIES("rarities"),

	SETS("sets"),

	TYPES("types");

	private String expand;

	private Expander(String expand) {
		this.expand = expand;
	}

	public String getExpand() {
		return expand;
	}

}