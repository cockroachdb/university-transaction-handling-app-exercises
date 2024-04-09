package com.cockroachlabs.university;

import java.util.UUID;

class ItemNotFound extends RuntimeException {

	public ItemNotFound(UUID itemId) {
		super("Could not find item '" + itemId + "'!");
	}
}
