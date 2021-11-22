package ru.kildishev.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.kildishev.service.api.StoreApi;

@FeignClient("stores")
public interface StoreClient extends StoreApi {
}
