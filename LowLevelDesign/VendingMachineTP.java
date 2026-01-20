package TargetedPractice.VendingMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class Product {
    String id;
    String name;
    double cost;
    int selectionNo;

    public Product(String name, double cost, int selectionNo) {
        this.id = "PDCT_" + UUID.randomUUID().toString();
        this.name = name;
        this.cost = cost;
        this.selectionNo = selectionNo;
    }
}

class ProductService {
    private static volatile ProductService instance;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    private ProductService(ProductRepository productRepository, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    public static ProductService getInstance(ProductRepository productRepository, InventoryService inventoryService) {
        if (instance == null) {
            synchronized (ProductService.class) {
                if (instance == null)
                    instance = new ProductService(productRepository, inventoryService);
            }
        }
        return instance;
    }

    public void addProduct(Product product) {
        productRepository.save(product);
        Inventory inventory = inventoryService.findInventoryByProduct(product);
        if (inventory == null) {
            inventoryService.addInventory(new Inventory(product, 1));
            return;
        }
        inventory.quantity += 1;
        inventoryService.addInventory(inventory);
    }
}

class ProductRepository {
    private Map<String, Product> productMap = new HashMap<>();

    public void save(Product product) {
        productMap.put(product.id, product);
    }
}

class Inventory {
    String id;
    Product product;
    double quantity;

    public Inventory(Product product, double quantity) {
        this.id = "I_" + UUID.randomUUID().toString();
        this.product = product;
        this.quantity = quantity;
    }
}

class InventoryService {
    private static volatile InventoryService instance;
    private final InventoryRepository inventoryRepository;

    private InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public static InventoryService getInstance(InventoryRepository repository) {
        if (instance == null) {
            synchronized (InventoryService.class) {
                if (instance == null) {
                    instance = new InventoryService(repository);
                }
            }
        }
        return instance;
    }

    public void addInventory(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    public Inventory findInventoryByProduct(Product product) {
        return inventoryRepository.findInventoryByProduct(product);
    }

    public Inventory findInventoryBySelectedNo(int selectedNo) {
        return inventoryRepository.findInventoryBySelectedNo(selectedNo);
    }

    public Inventory deductInventory(Inventory inventory) {
        inventory.quantity -= 1;
        return inventoryRepository.update(inventory);
    }
}

class InventoryRepository {
    private final Map<String, Inventory> inventoryMap = new HashMap<>();

    public void save(Inventory inventory) {
        inventoryMap.put(inventory.id, inventory);
    }

    public void remove(Inventory inventory) {
        inventoryMap.remove(inventory.id);
    }

    public Inventory findInventoryByProduct(Product product) {
        return inventoryMap.values().stream().filter(inventory -> inventory.product.id.equals(product.id)).findFirst().orElse(null);
    }

    public Inventory findInventoryBySelectedNo(int selectedNo) {
        return inventoryMap.values().stream().filter(inventory -> inventory.product.selectionNo == selectedNo).findFirst().orElse(null);
    }

    public Inventory update(Inventory inventory) {
        inventoryMap.put(inventory.id, inventory);
        return inventory; // Added missing return
    }
}

interface VendingMachineState {
    void insertMoney(VendingMachine vendingMachine, int amount);
    void dispense(VendingMachine vendingMachine, int selectedNo);
}

class ReadyState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vendingMachine, int amount) {
        vendingMachine.money.updateAndGet(current -> current + amount);
        System.out.println("Added " + amount + "rs! Total: " + vendingMachine.money);
        vendingMachine.vendingMachineState = new MoneyInsertState();
    }

    @Override
    public synchronized void dispense(VendingMachine vendingMachine, int selectedNo) {
        System.out.println("Pls insert Money first!");
    }
}

class MoneyInsertState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vendingMachine, int amount) {
        if (amount <= 0) {
            System.out.println("Pls enter valid amount!");
            return;
        }
        vendingMachine.money.updateAndGet(current -> current + amount);
        System.out.println("Added " + amount + "rs! Total: " + vendingMachine.money);
    }

    @Override
    public synchronized void dispense(VendingMachine vendingMachine, int selectedNo) {
        vendingMachine.vendingMachineState = new DispenseState();
        vendingMachine.vendingMachineState.dispense(vendingMachine, selectedNo);
    }
}

class DispenseState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine vendingMachine, int amount) {
        vendingMachine.money.updateAndGet(current -> current + amount);
        System.out.println("Added " + amount + "rs!");
    }

    @Override
    public synchronized void dispense(VendingMachine vendingMachine, int selectedNo) {
        Inventory inventory = vendingMachine.inventoryService.findInventoryBySelectedNo(selectedNo);

        if (inventory == null) {
            System.out.println("Invalid Selection!");
            vendingMachine.vendingMachineState = new MoneyInsertState();
            return;
        }

        Product product = inventory.product;
        System.out.println("Selected Product: " + product.name);

        if (inventory.quantity <= 0) {
            System.out.println("Product " + product.name + " is out of stock!");
            vendingMachine.vendingMachineState = new MoneyInsertState();
            return;
        }

        if (vendingMachine.money.get() >= product.cost) {
            System.out.println("Dispensing " + product.name + "...");
            vendingMachine.inventoryService.deductInventory(inventory);
            vendingMachine.money.updateAndGet(current -> current - product.cost);

            System.out.println("Remaining Balance: " + vendingMachine.money);

            if (vendingMachine.money.get() > 0) {
                System.out.println("Returning change: " + vendingMachine.money);
                vendingMachine.money.set(0.0);
            }
            vendingMachine.vendingMachineState = new ReadyState();
        } else {
            System.out.println("Insufficient funds! Needs " + (product.cost - vendingMachine.money.get()) + " more.");
            vendingMachine.vendingMachineState = new MoneyInsertState();
        }
    }
}

class VendingMachine {
    private static volatile VendingMachine instance;
    AtomicReference<Double> money;
    VendingMachineState vendingMachineState;
    InventoryService inventoryService;

    private VendingMachine() {
        money = new AtomicReference<>(0.0);
        vendingMachineState = new ReadyState();
        inventoryService = InventoryService.getInstance(new InventoryRepository());
    }

    public static VendingMachine getInstance() {
        if (instance == null) {
            synchronized (VendingMachine.class) {
                if (instance == null) instance = new VendingMachine();
            }
        }
        return instance;
    }

    public synchronized void addMoney(int amount) {
        vendingMachineState.insertMoney(this, amount);
    }

    public synchronized void dispense(int selectedNo) {
        vendingMachineState.dispense(this, selectedNo);
    }
}

public class Solution {
    public static void main(String[] args) {
        VendingMachine vm = VendingMachine.getInstance();

        // Populate products
        ProductService ps = ProductService.getInstance(new ProductRepository(), vm.inventoryService);
        ps.addProduct(new Product("Coke", 25.0, 1));
        ps.addProduct(new Product("Pepsi", 35.0, 2));
        ps.addProduct(new Product("Soda", 10.0, 3));

        System.out.println("--- Test Case 1: Successful Purchase ---");
//        vm.addMoney(20);
//        vm.addMoney(10);
//        vm.dispense(1); // Should dispense Coke (25) and return 5 change
//        vm.addMoney(20);
//        vm.addMoney(10);
//        vm.dispense(1);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                vm.addMoney(25);
                vm.dispense(1);
            });
        }

//        System.out.println("\n--- Test Case 2: Insufficient Funds ---");
//        vm.addMoney(10);
//        vm.dispense(2); // Needs 35
    }
}
