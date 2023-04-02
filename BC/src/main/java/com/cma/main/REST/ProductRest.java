package com.cma.main.REST;

import com.cma.main.Wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "add")
    ResponseEntity<String> addProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProducts();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable String id);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateProductStatus(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/getByCategory/{categoryId}")
    ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable String categoryId);

    @PostMapping(path = "/getProduct/{productId}")
    ResponseEntity<ProductWrapper> getById(@PathVariable String productId);

}
