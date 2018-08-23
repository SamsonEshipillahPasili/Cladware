package com.cladware.entities;

import com.cladware.dto.Cart;
import lombok.Data;

@Data
public class Receipt {

    private CladwareOrder order;
    private String receiptId;
    private String phone;
    private String paymentMethod;
    private String country;
    private String postalAddress;

    public Receipt(){
        Cart cart = new Cart();
        CladwareOrder cladwareOrder = new CladwareOrder();
        cladwareOrder.setCart(cart);
        order = cladwareOrder;
    }
}
