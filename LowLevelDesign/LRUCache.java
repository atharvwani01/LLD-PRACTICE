package TargetedPractice.LRUCache;

import java.util.HashMap;
import java.util.Map;

class Node<T, U>{
    T key;
    U value;
    Node<T, U> prev;
    Node<T, U> next;

    public Node(T key, U value){
        this.key = key;
        this.value = value;
        this.prev = null;
        this.next = null;
    }
}

class DLL<T, U>{
    Node<T, U> head;
    Node<T, U> tail;
    public DLL(){
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
}

class LRUCache<T, U>{
    int capacity;
    DLL<T, U> dll;
    Map<T, Node<T, U>> values = new HashMap<>();
    public LRUCache(int capacity){
        this.capacity = capacity;
        dll = new DLL<>();
    }
    private void deleteNode(Node<T, U> node){
        Node<T, U> prevNode = node.prev;
        Node<T, U> nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }
    private void addNode(Node<T, U> node){
        Node<T, U> nxtNode = dll.head.next;
        dll.head.next = node;
        node.next = nxtNode;
        node.prev = dll.head;
        nxtNode.prev = node;
    }
    public synchronized void put(T key, U value){
        if(values.containsKey(key)){
            Node<T, U> node = values.get(key);
            deleteNode(node);
            node.value = value;
            values.put(node.key, node);
            addNode(node);
        } else if (values.size() == capacity) {
            values.remove(dll.tail.prev.key);
            deleteNode(dll.tail.prev);
            Node<T, U> newNode = new Node<>(key, value);
            values.put(newNode.key, newNode);
            addNode(newNode);
        }
        else{
            Node<T, U> newNode = new Node<>(key, value);
            values.put(newNode.key, newNode);
            addNode(newNode);
        }
    }

    public synchronized U value(T key){
        if(values.containsKey(key)){
            Node<T, U> node = values.get(key);
            deleteNode(node);
            addNode(node);
            return node.value;
        }
        else
            return null;
    }

}
public class Solution {
    public static void main(String[] args) {
        // Cache capacity of 2 for testing eviction
        LRUCache<String, String> sessionCache = new LRUCache<>(2);

        // Scenario: Multiple threads (Elevators) accessing the same cache
        Runnable task1 = () -> {
            sessionCache.put("User_A", "VIP_GOLD");
            System.out.println("Thread 1: Added User_A");
        };

        Runnable task2 = () -> {
            sessionCache.put("User_B", "REGULAR");
            System.out.println("Thread 2: Added User_B");
        };

        Runnable task3 = () -> {
            // This should trigger eviction of the oldest user (User_A)
            sessionCache.put("User_C", "VIP_PLATINUM");
            System.out.println("Thread 3: Added User_C (Should evict oldest)");
        };

        // Start threads
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);
        Thread t3 = new Thread(task3);

        t1.start();
        t2.start();
        t3.start();

        // Wait for them to finish
        try {
            t1.join(); t2.join(); t3.join();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // Verify results
        System.out.println("Final Cache Check:");
        System.out.println("User_A: " + sessionCache.value("User_A")); // Likely null (evicted)
        System.out.println("User_B: " + sessionCache.value("User_B")); // Should be present
        System.out.println("User_C: " + sessionCache.value("User_C")); // Should be present
    }
}
