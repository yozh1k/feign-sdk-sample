package ru.kildishev.service.api;

import org.springframework.web.bind.annotation.*;
import ru.kildishev.service.api.dto.Store;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;


public interface StoreApi {
    /**
     * List of all stores
     */
    @GetMapping("stores")
    Collection<Store> getStores();

    /**
     * Search for store
     * @param storeId - id of the store to find
     * @return - found store oremtpy Optional
     */
    @GetMapping("stores/{storeId}")
    Optional<Store> find(@PathVariable("storeId") UUID storeId);

    /**
     * Create or update store
     */
    @PostMapping("stores")
    void add(@RequestBody Store store);
}
