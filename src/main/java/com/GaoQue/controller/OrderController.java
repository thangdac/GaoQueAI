package com.GaoQue.controller;

import com.GaoQue.service.order.OrderService;
import com.GaoQue.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/order/success")
    public String orderSuccess() {
        return "/Order/success";
    }

    @PostMapping("/order/submit")
    public String placeOrder(
            @RequestParam Long userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam String paymentMethod,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(userId, firstName, lastName, email, address, phoneNumber);
            orderService.placeOrder(userId, paymentMethod);

            redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công!");
            return "redirect:/order/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình đặt hàng: " + e.getMessage());
            return "redirect:/order/confirm";
        }
    }

}

