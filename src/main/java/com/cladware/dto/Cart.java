package com.cladware.dto;

import com.cladware.entities.Item;

import java.io.Serializable;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Cart implements Serializable {
    // map of items, with item code as key
    private final Map<String, Item> cartMap;

    private int sameCount = 0;

    public Cart() {
        this.cartMap = new HashMap<>();
    }

    public int getSize() {
        return this.cartMap.size() + this.sameCount;
    }

    public Collection<Item> getItems() {
        return cartMap.values().stream().flatMap(item -> {
            List<Item> itemList = new ArrayList<>();
            for (int i = 0; i < item.getQuantity(); i++) {
                itemList.add(item);
            }
            return itemList.stream();
        }).collect(Collectors.toList());
    }

    // delete an item from the cart
    public void deleteItem(String code) {

        if (this.cartMap.containsKey(code)) {
            Item item = this.cartMap.get(code);
            if (item.getQuantity() == 1) {
                this.cartMap.remove(code);
            } else {
                item.setQuantity(item.getQuantity() - 1);
                this.cartMap.put(code, item);
                this.sameCount--;
            }
        }

    }


    public void addToCart(String code, Item item) {
        String newCode = String.format("Cart_%s", code);

        if (this.cartMap.containsKey(newCode.trim())) {
            Item temp = this.cartMap.get(newCode);
            // increment item's quantity
            temp.setQuantity(temp.getQuantity() + 1);
            this.sameCount++;
        } else {
            Item newItem = new Item();
            newItem.setName(item.getName());
            newItem.setUnitPrice(item.getUnitPrice());
            newItem.setCode(newCode);
            newItem.setQuantity(1);
            newItem.setDescription(item.getDescription());
            //newItem.setImageData(item.getImageData());

            // item.setQuantity(1);

            // this.cartMap.put(code, item);
            this.cartMap.put(newCode, newItem);
        }
    }

    public int getTotal() {
        return this.getItems().stream().mapToInt(Item::getUnitPrice).sum();
    }

    public void clearCart() {
        this.cartMap.clear();
        this.sameCount = 0;
    }


}
