package com.cma.main.ServiceImpl;

import com.cma.main.Constants.Constants;
import com.cma.main.DAO.ProductDAO;
import com.cma.main.JWT.JwtFilter;
import com.cma.main.POJO.Category;
import com.cma.main.POJO.Product;
import com.cma.main.Service.ProductService;
import com.cma.main.Utils.CafeUtils;
import com.cma.main.Wrapper.ProductWrapper;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDAO productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("categoryId") && requestMap.containsKey("productName") && requestMap.containsKey("description") && requestMap.containsKey("price")) {
                    if (!Strings.isNullOrEmpty(requestMap.get("categoryId")) && !Strings.isNullOrEmpty(requestMap.get("productName")) && !Strings.isNullOrEmpty(requestMap.get("description")) && !Strings.isNullOrEmpty(requestMap.get("price"))) {
                        Product productFromName = productDao.findFirstByProductNameContainingIgnoreCase(requestMap.get("productName"));
                        if (Objects.isNull(productFromName)) {
                            Product product = new Product();
                            Category categoryObj = new Category();

                            categoryObj.setId(Integer.parseInt(requestMap.get("categoryId")));

                            product.setProductName(requestMap.get("productName"));
                            product.setDescription(requestMap.get("description"));
                            product.setPrice(Integer.parseInt(requestMap.get("price")));
                            product.setStatus("true");
                            product.setCategory(categoryObj);

                            productDao.save(product);

                            return CafeUtils.getResponse(Constants.PRODUCT_ADD_SUCCESS, HttpStatus.OK);
                        }
                        return CafeUtils.getResponse(Constants.ALREADY_EXISTS, HttpStatus.FOUND);
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

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try {
            return new ResponseEntity<>(productDao.getAllProducts(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (requestMap.containsKey("productName") && requestMap.containsKey("id") && requestMap.containsKey("description") && requestMap.containsKey("price")) {
                    if (!Strings.isNullOrEmpty(requestMap.get("productName")) && !Strings.isNullOrEmpty(requestMap.get("id")) && !Strings.isNullOrEmpty(requestMap.get("description")) && !Strings.isNullOrEmpty(requestMap.get("price"))) {
                        Product productObj = productDao.findFirstByProductNameContainingIgnoreCase(requestMap.get("productName"));
                        Product productFromId = productDao.findFirstById(Integer.parseInt(requestMap.get("id")));
                        if (Objects.isNull(productObj)) {
                            return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
                        } else if (Objects.isNull(productFromId)) {
                            return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
                        } else if (!productFromId.getProductName().equalsIgnoreCase(productObj.getProductName())) {
                            return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                        } else if (productObj.getProductName().equals(requestMap.get("productName")) && productObj.getDescription().equals(requestMap.get("description")) && productObj.getPrice().equals(Integer.parseInt(requestMap.get("price")))) {
                            return CafeUtils.getResponse(Constants.ALREADY_EXISTS, HttpStatus.FOUND);
                        } else {
                            productObj.setProductName(requestMap.get("productName"));
                            productObj.setDescription(requestMap.get("description"));
                            productObj.setPrice(Integer.parseInt(requestMap.get("price")));
                            productDao.save(productObj);
                            return CafeUtils.getResponse(Constants.PRODUCT_UPDATE_SUCCESS, HttpStatus.OK);
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

    @Override
    public ResponseEntity<String> deleteProduct(String id) {
        try {
            if (jwtFilter.isAdmin()) {
                Product productObj = productDao.findFirstById(Integer.parseInt(id));
                if (!Objects.isNull(productObj)) {
                    productDao.deleteById(Integer.parseInt(id));
                    return CafeUtils.getResponse(Constants.PRODUCT_DELETE_SUCCESS, HttpStatus.OK);
                }
                return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
            }
            return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Product productObj = productDao.findFirstById(Integer.parseInt(requestMap.get("id")));
                if (Objects.nonNull(productObj) && productObj.getProductName().equalsIgnoreCase(requestMap.get("productName"))) {
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponse(Constants.PRODUCT_UPDATE_SUCCESS, HttpStatus.OK);
                }
                return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
            }
            return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("ProductServImpl");
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(String categoryId) {
        try {
            List<ProductWrapper> productWrapperList = productDao.getProductsByCategoryId(Integer.parseInt(categoryId));
            return new ResponseEntity<>(productWrapperList, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("ProductServImpl");
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(String productId) {
        try {
            ProductWrapper productWrapperObj = productDao.getProductById(Integer.parseInt(productId));
            return new ResponseEntity<>(productWrapperObj, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("ProductServImpl");
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
