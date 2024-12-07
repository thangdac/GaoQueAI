package com.GaoQue.controller;

import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Cart;
import com.GaoQue.model.Order;
import com.GaoQue.model.OrderItem;
import com.GaoQue.model.User;
import com.GaoQue.service.cart.CartService;
import com.GaoQue.service.order.OrderService;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    // Xóa giỏ hàng từ session
    @PostMapping("/clear")
    public String clearCart(HttpSession session, Model model) {
        try {
            // Lấy cartId từ session
            Long cartId = (Long) session.getAttribute("cartId");

            if (cartId == null) {
                model.addAttribute("error", "Giỏ hàng không tồn tại.");
                return "redirect:/cart";
            }
            cartService.clearCart(cartId);
            model.addAttribute("message", "Giỏ hàng đã được xóa thành công.");
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Giỏ hàng không tồn tại.");
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/order")
    public String showOrderConfirmation(@RequestParam Long userId, Model model) {
        // Lấy thông tin người dùng
        User user = userService.getUserById(userId);

        // Đặt đơn hàng
        Order order = orderService.placeOrder(userId);

        // Lấy danh sách các sản phẩm trong đơn hàng
        List<OrderItem> orderItems = order.getOrderItems().stream().toList();

        return "Order/OrderDetail"; // Trang xác nhận đơn hàng
    }





}
