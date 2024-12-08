package com.GaoQue.controller;

import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Cart;
import com.GaoQue.model.User;
import com.GaoQue.service.cart.CartService;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }


    // Xóa giỏ hàng
    @PostMapping("/clear")
    public String clearCart(HttpSession session,RedirectAttributes redirectAttributes) {
        try {
            // Lấy cartId từ session
            Long cartId = (Long) session.getAttribute("cartId");

            if (cartId == null) {
                redirectAttributes.addFlashAttribute("error", "Giỏ hàng không tồn tại.");
                return "redirect:/cart";
            }
            cartService.clearCart(cartId);
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được xóa thành công.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng không tồn tại.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    //view Mua Hàng
    @GetMapping("/order")
    public String checkoutDetails(HttpSession session, Model model,
                                  RedirectAttributes redirectAttributes, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        Long cartId = (Long) session.getAttribute("cartId");

        // Kiểm tra nếu giỏ hàng không tồn tại hoặc không có sản phẩm
        if (cartId == null || cartService.getCart(cartId) == null || cartService.getCart(cartId).getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không có sản phẩm trong gỏi hàng!!");
            return "redirect:/cart";
        }

        // Lấy thông tin giỏ hàng
        Cart cart = cartService.getCart(cartId);
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        return "Order/OrderDetail";
    }

}
