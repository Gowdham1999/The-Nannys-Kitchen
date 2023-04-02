package com.cma.main.POJO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Data
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serializable = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String productName;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category")
    private Category category;

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    private String status;
}
