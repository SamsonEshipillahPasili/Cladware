package com.cladware.controllers;

import com.cladware.dto.Cart;
import com.cladware.entities.CladwareOrder;
import com.cladware.entities.Item;
import com.cladware.repositories.ItemRepository;
import com.cladware.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CladwareRestController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;

    // resolve item images given an item code
    @GetMapping("/item/img/{code}/")
    public ResponseEntity<byte[]> getItemImage(@PathVariable String code){
        Optional<Item> itemOptional = this.itemRepository.findById(code);
        if(itemOptional.isPresent()){
            byte[] data = itemOptional.get().getImageData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        }else{
            return null;
        }
    }

    // expose all orders
    @GetMapping("/orderCart/{orderId}")
    public Cart getOrderCart(@PathVariable Long orderId){
        Cart cart = this.orderRepository.findById(orderId).get().getCart();
        return cart;
    }

    @GetMapping("/getOrder/{id}")
    public CladwareOrder getOrderById(@PathVariable Long id){
        return this.orderRepository.findById(id).orElse(null);
    }

    // get an item given the code
    @GetMapping("/item/{code}")
    public Item getByCode(@PathVariable  String code){
        Optional<Item> itemOptional = this.itemRepository.findById(code);
        // return null if the item does not exist.
        return itemOptional.orElse(null);
    }


    @PostMapping("/cancelOrder/{id}")
    public String cancelOrder(@PathVariable Long id){

        final Optional<CladwareOrder> orderOptional = this.orderRepository.findById(id);
        if(orderOptional.isPresent()){
            // proceed to mark it as canceled
            CladwareOrder cladwareOrder = orderOptional.get();
            if(cladwareOrder.getStatus().equalsIgnoreCase("Cancelled")){
                return "Order is already cancelled";
            }
            cladwareOrder.setStatus("Cancelled");
            this.orderRepository.save(cladwareOrder);
            return "Ok";
        }else{
            // redirect to the manage orders page, safely.
            return "There is no order with the specified ID";
        }
    }

    @PostMapping("/deliverOrder/{id}")
    public String deliverOrder(@PathVariable Long id){
        final Optional<CladwareOrder> orderOptional = this.orderRepository.findById(id);
        if(orderOptional.isPresent()){
            // check if the order has been cancelled.
            CladwareOrder cladwareOrder = orderOptional.get();
            if(cladwareOrder.getStatus().equalsIgnoreCase("Cancelled")){
                return "You cannot deliver a cancelled order!";
            }else{
                cladwareOrder.setStatus("Delivered");
                this.orderRepository.save(cladwareOrder);
                return "Ok";
            }
        }else{
            // redirect to the manage orders page, safely.
            return "There is no order with the specified ID";
        }
    }
}
