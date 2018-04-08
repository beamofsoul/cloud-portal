//package com.moraydata.general.primary.controller.openapi;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequestMapping("/open")
//@RestController
//public class TestEndpoints {
//
//    @GetMapping("/product/{id}")
//    public String getProduct(@PathVariable String id) {
//        SecurityContextHolder.getContext().getAuthentication();
//        return "product id : " + id;
//    }
//
//    @GetMapping("/order/{id}")
//    public String getOrder(@PathVariable String id) {
//        SecurityContextHolder.getContext().getAuthentication();
//        return "order id : " + id;
//    }
//
//}
