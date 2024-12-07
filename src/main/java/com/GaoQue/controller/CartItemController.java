package com.GaoQue.controller;

import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Cart;
import com.GaoQue.model.User;
import com.GaoQue.repository.CartRepository;
import com.GaoQue.service.cart.ICartItemService;
import com.GaoQue.service.cart.ICartService;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartItemController {

    private final ICartItemService cartItemService;
    private final ICartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;


    public CartItemController(ICartItemService cartItemService, ICartService cartService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
    }

    @PostMapping("/item/add")
    public String addItemToCart(@RequestParam(required = false) Long cartId,
                                @RequestParam Long productId,
                                @RequestParam Integer quantity,
                                HttpSession session,
                                Model model) {
        try {
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String username = authentication.getName();
                    User user = userService.findByUser(username);
                    if (user != null) {
                        userId = user.getId();
                        session.setAttribute("userId", userId);
                    }
                } else {
                    return "redirect:/login";
                }
            }
            if (cartId == null) {
                cartId = cartService.initializeNewCart(userId);  // Truyền userId vào để gán cho giỏ hàng mới
                session.setAttribute("cartId", cartId);  // Lưu cartId vào session
            }
            cartItemService.addItemToCart(cartId, productId, quantity);

            // Cập nhật lại giỏ hàng và tổng tiền
            Cart cart = cartService.getCart(cartId);
            model.addAttribute("cart", cart);
            model.addAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!");

            return "redirect:/Product"; // Chuyển hướng đến trang giỏ hàng

        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Sản phẩm không tìm thấy.");
            return "redirect:/Product"; // Quay lại trang giỏ hàng với thông báo lỗi
        }
    }

    @PostMapping("/{cartId}/item/{itemId}/remove")
    public String removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId, Model model) {
        try {
            // Gọi service để xóa sản phẩm khỏi giỏ hàng
            cartItemService.removeItemFromCart(cartId, itemId);

            // Lấy lại giỏ hàng đã được cập nhật từ cơ sở dữ liệu
            Cart updatedCart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng không tồn tại"));

            // Thêm giỏ hàng đã cập nhật vào model
            model.addAttribute("cart", updatedCart);

            // Thêm thông báo thành công
            model.addAttribute("message", "Sản phẩm đã được xóa khỏi giỏ hàng.");

            // Trả về tên view để hiển thị lại giỏ hàng
            return "redirect:/cart";  // Render lại trang cart.html
        } catch (ResourceNotFoundException ex) {
            // Thêm thông báo lỗi vào model
            model.addAttribute("error", ex.getMessage());

            // Quay lại trang giỏ hàng với thông báo lỗi
            return "redirect:/cart";  // Render lại trang cart.html
        }
    }

}
