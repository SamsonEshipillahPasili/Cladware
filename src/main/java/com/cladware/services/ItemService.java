package com.cladware.services;

import com.cladware.entities.Item;
import com.cladware.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public void addItem(String code, String name, int unitPrice,
                        int quantity, String description, byte[] imageData) {
        if (this.itemRepository.existsById(code)) {
            // item already exists, just update the values
            Item item = this.itemRepository.findById(code).get();
            item.setName(name);
            item.setUnitPrice(unitPrice);
            item.setQuantity(item.getQuantity() + quantity);
            item.setDescription(description);
            if (imageData != null)
                item.setImageData(imageData);

            this.itemRepository.save(item);
        } else {
            // create a new one
            Item item = new Item();
            item.setCode(code);
            item.setName(name);
            item.setUnitPrice(unitPrice);
            item.setQuantity(item.getQuantity() + quantity);
            item.setDescription(description);
            item.setImageData(imageData);

            this.itemRepository.save(item);
        }
    }

    private Iterable<Item> filterEmptyItems(Iterable<Item> items){
        List<Item> processedItems = new ArrayList<>();
        items.forEach(processedItems::add);
        return processedItems.stream().filter(item -> item.getQuantity() > 0).collect(Collectors.toList());
    }


    // group the items into groups of four
    public List<List<Item>> group(Iterable<Item> items) {
        items = this.filterEmptyItems(items);
        List<List<Item>> itemList = new ArrayList<>();
        List<Item> currentItemList = new ArrayList<>();

        for (Item item : items) {
            if (currentItemList.size() >= 4) {
                ArrayList<Item> temp = new ArrayList<>();
                temp.addAll(currentItemList);
                currentItemList.clear();
                itemList.add(temp);
                currentItemList.add(item);
            } else {
                currentItemList.add(item);
            }
        }
        // collect any remaining items in the currentItemList and add them to itemList
        itemList.add(currentItemList);

        return itemList;

    }

}
