package ru.kildishev.service.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.kildishev.service.api.StoreApi;
import ru.kildishev.service.api.dto.Store;

import java.util.*;

@RestController
public class StoreController implements StoreApi {

    private final Map<UUID, Store> stores = new HashMap<>();

    @Override
    public Collection<Store> getStores() {
        return stores.values();
    }

    @Override
    public Optional<Store> find(UUID storeId) {
        return Optional.ofNullable(stores.get(storeId));
    }

    @Override
    public void add(Store store) {
        stores.put(store.getId(), store);
    }
}
