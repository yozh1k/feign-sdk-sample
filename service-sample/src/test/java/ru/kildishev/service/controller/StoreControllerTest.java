package ru.kildishev.service.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.kildishev.service.api.dto.Store;
import ru.kildishev.service.client.StoreClient;

import java.util.Collection;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients(basePackageClasses = StoreClient.class)
class StoreControllerTest {

    @Autowired
    StoreClient storeClient;

    @Test
    void testPutAndFind() {
        Store store = new Store(UUID.randomUUID(), "Magic Store", "Agrabah, Main Square, 1");
        Assertions.assertFalse(storeClient.find(store.getId()).isPresent());
        storeClient.add(store);
        Assertions.assertTrue(storeClient.find(store.getId()).isPresent());
    }

    @Test
    void testPutAndGetAll() {
        Store openedStore = new Store(UUID.randomUUID(), "Magic Store", "Agrabah, Main Square, 1");
        Store closedStore = new Store(UUID.randomUUID(), "AwesomeStore", "221B, Baker Street, London");

        Collection<Store> feignAllStores = storeClient.getStores();
        Assertions.assertFalse(feignAllStores.contains(openedStore));
        Assertions.assertFalse(feignAllStores.contains(closedStore));

        storeClient.add(openedStore);
        storeClient.add(closedStore);

        feignAllStores = storeClient.getStores();
        Assertions.assertTrue(feignAllStores.contains(openedStore));
        Assertions.assertTrue(feignAllStores.contains(closedStore));
    }

}
