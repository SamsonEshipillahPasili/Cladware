package com.cladware.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

@Entity
@Data
public class Item implements Serializable {
    @Column @Id
    private String code;
    @Column
    private String name;
    @Column
    private int unitPrice;
    @Column
    private int quantity;
    @Column
    private String description;
    @Column @Lob
    private byte[] imageData;

    // return URI to image
    public String imagePath(){
        return String.format("/item/img/%s/", this.code);
    }
}
