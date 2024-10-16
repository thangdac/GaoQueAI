package com.GaoQue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class ProductController {
    @GetMapping("/Product")
    public String product(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với sản phẩm!");
        return "Product/product"; // Trả về trang "product"
    }
}
