package com.GaoQue.controller;

import com.GaoQue.dto.ProductDto;
import com.GaoQue.model.Cart;
import com.GaoQue.model.CartItem;
import com.GaoQue.model.Product;
import com.GaoQue.model.User;
import com.GaoQue.service.cart.CartItemService;
import com.GaoQue.service.cart.CartService;
import com.GaoQue.service.product.ProductService;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Controller
public class PageController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemService cartItemService;



    @ModelAttribute("user")
    public User addUserToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            return userService.findByUser(email);
        }
        return null;
    }

    @GetMapping("/")
    public String layout(Model model, @RequestParam(value = "message", required = false) String message) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("message", "Đăng nhập thành công!");
        } else {
            model.addAttribute("error", "Đăng xuất thành công!");
        }

        return "layout"; // Đảm bảo trả về đúng view
    }

    //view home
    @GetMapping("/Home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang chủ!");
        return "Home/home";
    }

    //view product
    @GetMapping("/Product")
    public String getAllProductsUser(Model model) {
        List<Product> products = productService.getAllProducts();

        Locale vietnamLocale = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);
        for (Product product : products) {
            product.setFormattedPrice(currencyFormatter.format(product.getPrice()));
        }
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        model.addAttribute("products", convertedProducts);

        model.addAttribute("message", "Danh sách sản phẩm đã được tải thành công!");
        return "/Product/product";
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/Product/AddToCart")
    public String addToCart(@RequestParam("cartId") Long cartId,
                            @RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            Model model) {
        try {
            // Thêm sản phẩm vào giỏ hàng
            cartItemService.addItemToCart(cartId, productId, quantity);
            model.addAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!");
        } catch (Exception e) {
            model.addAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
        return "redirect:/Product";  // Quay lại trang giỏ hàng
    }

    @GetMapping("/cart")
    public String getCart(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        // Kiểm tra nếu không có userId trong session, kiểm tra authentication từ Spring Security
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                // Lấy tên người dùng (thường là email)
                String username = authentication.getName();
                User user = userService.findByUser(username);
                if (user != null) {
                    userId = user.getId();
                    session.setAttribute("userId", userId);
                }
            } else {
                model.addAttribute("message", "Vui lòng đăng nhập để xem giỏ hàng.");
                return "redirect:/login"; // Chuyển hướng đến trang login
            }
        }

        // Lấy giỏ hàng của người dùng
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            model.addAttribute("message", "Giỏ hàng của bạn hiện tại trống.");
        } else {
            model.addAttribute("cart", cart);
            model.addAttribute("message", "Giỏ hàng của bạn đã được tải thành công.");
        }

        return "/Cart/Cart"; // Trả về trang giỏ hàng
    }

    //view classification
    @GetMapping("/AINhanDien")
    public String AINhanDien(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với AI Nhận Diện Gạo!");
        return "AIPrice/AINhanDien";
    }

}
