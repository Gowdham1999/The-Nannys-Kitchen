package com.cma.main.DAO;

import com.cma.main.POJO.Product;
import com.cma.main.Wrapper.ProductWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {

    Product findFirstByProductNameContainingIgnoreCase(String productName);

    Product findFirstById(Integer id);

    @Query("select new com.cma.main.Wrapper.ProductWrapper(p.id, p.productName, p.description, p.status, p.price, p.category.id, p.category.categoryName) from Product p")
    List<ProductWrapper> getAllProducts();

    @Transactional
    @Modifying
    @Query("update Product p set p.status =:status where p.id =:id")
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    @Query("select new com.cma.main.Wrapper.ProductWrapper(p.id, p.productName, p.description, p.price, p.status) from Product p where p.category.id =:categoryId")
    List<ProductWrapper> getProductsByCategoryId(Integer categoryId);

    @Query("select new com.cma.main.Wrapper.ProductWrapper(p.id, p.productName, p.description, p.price, p.status) from Product p where p.id =:productId")
    ProductWrapper getProductById(Integer productId);
}
