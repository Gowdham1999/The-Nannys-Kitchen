package com.cma.main.REST;

import com.cma.main.POJO.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bill")
public interface BillRest {

    @PostMapping(path = "/generate")
    ResponseEntity<String> generateBill(@RequestBody Map<String, Object> requestMap);

    @PostMapping(path = "/getBill")
    ResponseEntity<List<Bill>> getBill();

    @PostMapping(path = "/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap);

    @PostMapping(path = "/delete/{billId}")
    ResponseEntity<String> deleteBill(@PathVariable String billId);
}
