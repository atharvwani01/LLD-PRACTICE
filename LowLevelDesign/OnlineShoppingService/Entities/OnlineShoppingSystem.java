package LowLevelDesign.OnlineShoppingService.Entities;


import LowLevelDesign.CarRental.Entities.Customer;
import LowLevelDesign.OnlineShoppingService.Service.InventoryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineShoppingSystem {
    private static OnlineShoppingSystem instance;

    //Data stores
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    //Services
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final SearchService searchService;

    private OnlineShoppingSystem() {
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.orderService = new OrderService(inventoryService);
        this.searchService = new SearchService(products.values());
    }

    public static OnlineShoppingSystem getInstance() {
        if (instance == null) {
            instance = new OnlineShoppingSystem();
        }
        return instance;
    }

}
