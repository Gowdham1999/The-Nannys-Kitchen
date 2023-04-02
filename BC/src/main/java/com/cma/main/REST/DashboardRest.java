package com.cma.main.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/dashboard")
public interface DashboardRest {

    @PostMapping(path = "/details")
    ResponseEntity<Map<String, Object>> getDetails();
}
