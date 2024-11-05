package com.GaoQue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/")
    public String Layout(Model model) {
        model.addAttribute("content", "home");
        return "layout"; // layout chính
    }

    @GetMapping("/Home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang chủ!");
        return "Home/home"; // Trả về trang "home"
    }

    @GetMapping("/Product")
    public String product(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với sản phẩm!");
        return "Product/product"; // Trả về trang "product"
    }

    @GetMapping("/AINhanDien")
    public String AINhanDien(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với AI Nhận Diện Gạo!");
        return "AIPrice/AINhanDien"; // Trả về trang "AI Nhận Diện"
    }
}
