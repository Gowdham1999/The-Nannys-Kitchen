package com.cma.main.DAO;

import com.cma.main.POJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {

    List<Category> findAll();

    Category findFirstByCategoryNameContainingIgnoreCase(String categoryName);

    Category findFirstById(Integer id);

    List<Category> findCategoriesByProductStatus();

}
