package com.GaoQue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/Admin")
    public String admin(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang Admin!");
        return "Admin/index"; // Trả về trang "index"
    }

    @GetMapping("/Admin/buttons")
    public String buttons(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang button!");
        return "Admin/Components/buttons"; // Trả về trang "buttons"
    }

    @GetMapping("/Admin/cards")
    public String cards(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang card!");
        return "Admin/Components/cards"; // Trả về trang "cards"
    }

    @GetMapping("/Admin/charts")
    public String charts(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang charts!");
        return "Admin/Charts/charts"; // Trả về trang "charts"
    }

    @GetMapping("/Admin/tables")
    public String tables(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang tables!");
        return "Admin/Tables/tables"; // Trả về trang "tables"
    }

    @GetMapping("/Admin/forgotPassword")
    public String forgotPassword(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang forgotPassword!");
        return "Admin/Pages/forgotPassword"; // Trả về trang "register"
    }

    @GetMapping("/Admin/Page404")
    public String Page404(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang Page404!");
        return "Admin/Pages/Page404"; // Trả về trang "register"
    }

    @GetMapping("/Admin/blank")
    public String blank(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang blank!");
        return "Admin/Pages/blank";
    }

    @GetMapping("/Admin/Home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang AdminHome!");
        return "Admin/AdminHome/Home";
    }
}
