<h1>Java web-service SDK with Spring Cloud Feign</h1>

Every modern java-engineer develops web-services.  In some cases it's make sense to provide some SDK alongside. For active developing project it's very important to support SDK compatibility with the service. It could be achieved via integration tests(developed from the scratch or generated e.g. with Spring Cloud Contract) of course. But today I would like to share some trick to check contract violation with static analysis. 

This technique is suitable for homogeneous environment: it's assumed that both service and client are able to use Spring Framework.

Let's assume we have some dummy service with simple controller:

```java
    @RestController
    public class StoreController implements StoreApi { 

        private final Map<UUID, Store> stores = new HashMap<>();
            
        /**
         * List of all stores
         */
        @GetMapping("stores")        
        public Collection<Store> getStores() {
            return stores.values();
        }

        /**
         * Search for store
         * @param storeId - id of the store to find
         * @return - found store oremtpy Optional
         */
        @GetMapping("stores/{storeId}")    
           public Optional<Store> find(UUID storeId) {
               return Optional.ofNullable(stores.get(storeId));
           }   
           
        /**
         * Create or update store
         */
          @PostMapping("stores")
           public void add(Store store) {
               stores.put(store.getId(), store);
           }
       }
```

And we would like to provide SDK for this API. First of all, lets try to separate the contract and the implementation in couple simple step.

Step 1: extract the interface from controller.

```java
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

```

Step 2: move the interface and DTO classes to separate module.


Now I want to create SDK that implement the same contract as service. The simplest way is to use [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/) - declarative rest client library.  Actually the API already declared in api module. So all we need to create the client is just extend the interface to add some feign-specific annotation in separated module. 
```java
@FeignClient("stores")
public interface StoreClient extends StoreApi {
}
```
All we need to accomplish is to provide some autoconfiguration within related spring boot starter
```java
@Configuration
@ConditionalOnMissingBean(StoreClient.class)
@EnableFeignClients(basePackageClasses = StoreClient.class)
public class StoreClientAutoConfiguration {
}

```
So now SDK and service compatibility is controlled by shared based interface. 
The only known (for me) issue is that you can't use @RequestMapping anotation on controller class level.

Additionally feign provides client load balancing(with ribbon) and service discovery support(by eureka).
Moreover we could use sdk to test the controller.
```java
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
    
}
```
To provide Feign client local port value just add related property to test configuration e.g. via  test/resources/application.yaml:
```yaml
stores:
  ribbon:
    listOfServers: http://localhost:${local.server.port}
```

Example project is available on my github: https://github.com/yozh1k/feign-sdk-sample

That's all. Thanks for reading, hope it'll be usefull. 
