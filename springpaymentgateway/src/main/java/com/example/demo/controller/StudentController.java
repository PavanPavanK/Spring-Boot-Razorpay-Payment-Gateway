
package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.StudentOrder;
import com.example.demo.service.StudentService;

@Controller
public class StudentController {

    @Autowired
    private StudentService service;

    @Value("${razorpay.key.id}")
    private String razorPayKey;

    @GetMapping("/")
    public String init() {
        return "index";
    }

    @PostMapping(value = "/create-order", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(
            @ModelAttribute StudentOrder studentOrder) throws Exception {

        StudentOrder createdOrder = service.createOrder(studentOrder);

        Map<String, Object> response = new HashMap<>();

        response.put("key", razorPayKey);
        response.put("razorpayOrderId", createdOrder.getRzorpayOrderId());
        response.put("amount", Math.multiplyExact(createdOrder.getAmount(), 100));
        response.put("currency", "INR");
        response.put("name", createdOrder.getName());
        response.put("email", createdOrder.getEmail());
        response.put("contact", createdOrder.getPhno());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
