package ru.kildishev.service.client.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import ru.kildishev.service.client.StoreClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Configuration
@ConditionalOnMissingBean(StoreClient.class)
@EnableFeignClients(basePackageClasses = StoreClient.class)
public class StoreClientAutoConfiguration {
}
