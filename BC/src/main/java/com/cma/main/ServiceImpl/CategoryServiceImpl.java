package com.cma.main.ServiceImpl;

import com.cma.main.Constants.Constants;
import com.cma.main.DAO.CategoryDAO;
import com.cma.main.JWT.JwtFilter;
import com.cma.main.POJO.Category;
import com.cma.main.Service.CategoryService;
import com.cma.main.Utils.CafeUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                log.info("Entering into addCategory {}", requestMap);
                if (!requestMap.isEmpty()) {
                    log.info("Entering into !requestMap.isEmpty() Validation {} in CategoryServiceImpl");
                    Category category = categoryDAO.findFirstByCategoryNameContainingIgnoreCase(requestMap.get("categoryName"));
                    if (Objects.isNull(category)) {
                        Category saveCategory = new Category();
                        saveCategory.setCategoryName(requestMap.get("categoryName"));
                        categoryDAO.save(saveCategory);
                        return CafeUtils.getResponse(Constants.CATEGORY_ADD_SUCCESS, HttpStatus.OK);
                    }
                    return CafeUtils.getResponse(Constants.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        log.info("Entering into getAllCategory Service Implementation:===");
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Inside Filter Request Param - CategoryServiceImpl");
                return new ResponseEntity<>(categoryDAO.findCategoriesByProductStatus(), HttpStatus.OK);
            }
            log.info("Returning All Categories - CategoryServiceImpl");
            return new ResponseEntity<>(categoryDAO.findAll(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("id") && requestMap.containsKey("categoryName")) {
                    if (!Strings.isNullOrEmpty(requestMap.get("id")) && !Strings.isNullOrEmpty(requestMap.get("categoryName"))) {
                        Category categoryFromID = categoryDAO.findFirstById(Integer.parseInt(requestMap.get("id")));
                        Category categoryFromName =  categoryDAO.findFirstByCategoryNameContainingIgnoreCase(requestMap.get("categoryName"));
                        if (Objects.isNull(categoryFromID)) {
                            return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
                        } else if (!Objects.isNull(categoryFromName)) {
                            return CafeUtils.getResponse(Constants.ALREADY_EXISTS, HttpStatus.FOUND);
                        } else {
                            categoryFromID.setCategoryName(requestMap.get("categoryName"));
                            categoryDAO.save(categoryFromID);
                            return CafeUtils.getResponse(Constants.CATEGORY_UPDATE_SUCCESS, HttpStatus.OK);
                        }
                    }
                    return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
