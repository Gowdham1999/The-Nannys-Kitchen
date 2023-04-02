package com.cma.main.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Data
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "bill")
public class Bill implements Serializable {

    private static final Long serializable = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String contactNumber;

    @Column
    private String email;

    @Column
    private String paymentMethod;

    @Column
    private Integer total;

    @Column(columnDefinition = "json")
    private String productDetails;

    @Column
    private String createdBy;
}

