package com.cockroachlabs.university;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
public class KwikShopperApplicationIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(KwikShopperApplicationIntegrationTest.class);

	@Autowired private ItemRepository repository;

	@Autowired private ItemInventoryService service;

	@Autowired private JdbcClient jdbcClient;

	@BeforeEach
	void initialize() {
		jdbcClient //
				.sql("UPDATE items SET quantity = 200 WHERE name = 'foo'") //
				.update();
	}

	@Test
	void multithreadedUpdatesShouldGiveUp() throws ExecutionException, InterruptedException {

		Item savedItem = repository.findByName("foo");

		Callable<Boolean> updateItemInventoryThroughService = () -> {

			service.updateItemInventory(savedItem.getItemId(), 3);

			return true;
		};

		Callable<Boolean> updateItemInventoryThroughRepository = () -> {

			log.info("Giving the other transaction time to start...");
			Thread.sleep(500);

			for (int i = 0; i < 10; i++) {
				log.info("This transaction in thread '" + Thread.currentThread().getName()
						+ "' is meant to disrupt the other transaction and force a retry.");
				repository.updateItemByReducingQuantity(savedItem.getItemId(), 2);
				Thread.sleep(1000);
			}

			return true;
		};

		// when
		ExecutorService executor = Executors.newCachedThreadPool();

		Future<Boolean> servicedBasedFuture = executor.submit(updateItemInventoryThroughService);
		Future<Boolean> repositoryBasedFuture = executor.submit(updateItemInventoryThroughRepository);

		servicedBasedFuture.get();
		repositoryBasedFuture.get();

		// then
		assertThat(repository.findById(savedItem.getItemId()).map(Item::getQuantity)).contains(180);
	}
}
