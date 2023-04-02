package com.cma.main.ServiceImpl;

import com.cma.main.DAO.BillDAO;
import com.cma.main.DAO.CategoryDAO;
import com.cma.main.DAO.ProductDAO;
import com.cma.main.DAO.UserDAO;
import com.cma.main.Service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    BillDAO billDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    UserDAO userDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getDetails() {
        log.info("Inside getDetails - DashboardServiceImpl");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("users", userDAO.count());
            map.put("products", productDAO.count());
            map.put("categories", categoryDAO.count());
            map.put("bills", billDAO.count());
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
