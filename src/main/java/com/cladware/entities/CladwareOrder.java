package com.cladware.entities;

import com.cladware.dto.Cart;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class CladwareOrder implements Serializable{
    @Id
    @Column
    private String orderId;
    @Column
    private String name;
    @Column
    private String phoneNumber;
    @Column
    private String country;
    @Column
    private String address;
    @Column
    private String paymentMethod;
    @Column
    private int cardNumber;
    @Column
    private String status;
    @Column
    private Date date;
    @Column
    @Lob
    private Cart cart;


    public CladwareOrder(){
        this.date = new Date();
    }
}
