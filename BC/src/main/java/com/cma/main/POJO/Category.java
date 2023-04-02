package com.cma.main.POJO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.List;

@NamedQuery(name = "Category.findCategoriesByProductStatus", query = "select c from Category c where c.id in (select p.category from Product p where p.status = 'true')")

@Data
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serializable = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String categoryName;

    @Column
    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Product> products;

}
