package com.cockroachlabs.university;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KwikShopperApplicationIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(KwikShopperApplicationIntegrationTest.class);

    @Autowired
    private ItemRepository repository;

    @Autowired
    private ItemInventoryService service;

    @Test
    void insertsShouldWork() {

        // given
        Item item = new Item();
        item.setName("foo");
        item.setDescription("fang");
        item.setQuantity(200);

        // when
        Item savedItem = repository.saveAndFlush(item);

        // then
        assertThat(savedItem.getItemId()).isNotNull();
    }

    @Test
    void reducingQuantityOfInventoryShouldWork() throws InterruptedException {

        // given
        Item item = new Item();
        item.setName("foo");
        item.setDescription("fang");
        item.setQuantity(200);
        Item savedItem = repository.saveAndFlush(item);

        // when
        service.updateItemInventory(savedItem.getItemId(), 3);

        // then
        assertThat(repository.findById(savedItem.getItemId()).map(Item::getQuantity)).contains(197);
    }

    @Test
    void multithreadedUpdatesShouldGiveUp() throws ExecutionException, InterruptedException {

        // given
        Item item = new Item();
        item.setName("foo");
        item.setDescription("fang");
        item.setQuantity(200);
        Item savedItem = repository.saveAndFlush(item);
        assertThat(savedItem.getItemId()).isNotNull();

        Callable<Boolean> updateItemInventoryThroughService = () -> {

            service.updateItemInventory(savedItem.getItemId(), 3);

            return true;
        };

        Callable<Boolean> updateItemInventoryThroughRepository = () -> {

            log.info("Giving the other transaction time to start...");
            Thread.sleep(1000);

            for (int i=0; i<10; i++) {
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
