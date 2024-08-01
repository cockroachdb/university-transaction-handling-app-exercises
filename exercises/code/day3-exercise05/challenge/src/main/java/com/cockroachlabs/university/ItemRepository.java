package com.cockroachlabs.university;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

interface ItemRepository extends JpaRepository<Item, UUID> {

	Logger log = LoggerFactory.getLogger(ItemRepository.class);

	Item findByName(String name);

	@Transactional
	@Modifying
	@Query("""
			UPDATE Item item
			SET item.quantity = item.quantity - :quantityToReduce
			WHERE item.itemId = :itemId
			AND item.quantity >= :quantityToReduce
			""")
	void updateItemByReducingQuantity(UUID itemId, Integer quantityToReduce);

	@Transactional
	default void reduceItemQuantity(UUID itemId, int quantity) throws InterruptedException {

		Item itemToUpdate = findById(itemId) //
				.orElseThrow(() -> new ItemNotFound(itemId));

		log.info("Artificial 2 second delay in thread '" + Thread.currentThread().getName()
				+ "'! Gives time for another transaction to happen while this is still open");
		Thread.sleep(2000);

		updateItemByReducingQuantity(itemToUpdate.getItemId(), quantity);
	}
}
