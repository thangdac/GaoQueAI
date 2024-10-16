package com.GaoQue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/Home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang chủ!");
        return "Home/home"; // Trả về trang "home"
    }
}
