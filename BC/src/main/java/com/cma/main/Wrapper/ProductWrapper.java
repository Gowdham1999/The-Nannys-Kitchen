package com.cma.main.Wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapper {

    private Integer id;

    private String productName;

    private String description;

    private String status;

    private Integer price;

    private Integer categoryID;

    private String categoryName;

    public ProductWrapper(Integer id, String productName, String description, Integer price, String status) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.status = status;
    }

}
