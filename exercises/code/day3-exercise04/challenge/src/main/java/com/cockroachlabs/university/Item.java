package com.cockroachlabs.university;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "items")
public class Item {

	@Id //
	@GeneratedValue(strategy = GenerationType.UUID) //
	private UUID itemId;

	private String name;

	private String description;

	private int quantity;

	private double price;

	protected Item() {

		this.itemId = null;
		this.name = null;
		this.description = null;
	}

	public Item(String name, String description, int quantity, double price) {
		this(null, name, description, quantity, price);
	}

	public Item(UUID itemId, String name, String description, int quantity, double price) {

		this.itemId = itemId;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.price = price;
	}

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Item item = (Item) o;
		return quantity == item.quantity && Double.compare(price, item.price) == 0 && Objects.equals(itemId, item.itemId)
				&& Objects.equals(name, item.name) && Objects.equals(description, item.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, name, description, quantity, price);
	}

	@Override
	public String toString() {
		return "Item{" + "itemId=" + itemId + ", name='" + name + '\'' + ", description='" + description + '\''
				+ ", quantity=" + quantity + ", price=" + price + '}';
	}
}
