package com.GaoQue.controller;

import com.GaoQue.service.order.OrderService;
import com.GaoQue.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/order/submit")
    public String placeOrder(
            @RequestParam Long userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phoneNumber,
            Model model) {
        try {
            userService.updateUser(userId, firstName, lastName, email, address, phoneNumber);
            orderService.placeOrder(userId);
            model.addAttribute("message", "Đặt hàng thành công!");
            return "redirect:/order/success";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình đặt hàng: " + e.getMessage());
            return "redirect:order/confirm";
        }
    }


}

