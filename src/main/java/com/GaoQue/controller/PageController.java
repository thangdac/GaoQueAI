package com.GaoQue.controller;

import com.GaoQue.dto.ProductDto;
import com.GaoQue.model.Cart;
import com.GaoQue.model.Product;
import com.GaoQue.model.User;
import com.GaoQue.service.cart.CartItemService;
import com.GaoQue.service.cart.CartService;
import com.GaoQue.service.product.ProductService;
import com.GaoQue.service.user.UserService;
import jakarta.servlet.http.HttpSession;
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

    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemService cartItemService;

    public PageController(UserService userService, ProductService productService,
                          CartService cartService, CartItemService cartItemService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @ModelAttribute("user")
    public User addUserToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            return userService.findByUser(email);
        }
        return null;
    }

    //view page
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
        Locale vietnamLocale = Locale.of("vi", "VN");
        List<Product> products = productService.getAllProducts();
        List<Product> firstFiveProducts = products.size() > 5 ? products.subList(0, 5) : products;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);
        for (Product product : products) {
            product.setFormattedPrice(currencyFormatter.format(product.getPrice()));
        }
        List<ProductDto> convertedProducts = productService.getConvertedProducts(firstFiveProducts);
        model.addAttribute("products", convertedProducts);

        return "layout";
    }

    //view home
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang chủ!");
        return "Home/home";
    }

    //view service
    @GetMapping("/Services")
    public String service(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với dịch vụ!");
        return "/Services/services";
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
        return "/Product/product";
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/Product/AddToCart")
    public String addToCart(@RequestParam("cartId") Long cartId,
                            @RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            RedirectAttributes redirectAttributes ) {
        try {
            // Thêm sản phẩm vào giỏ hàng
            cartItemService.addItemToCart(cartId, productId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!");
        return "redirect:/Product";
    }

    //view Gỏi hàng
    @GetMapping("/cart")
    public String getCart(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

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
                return "redirect:/login";
            }
        }
        List<Product> products = productService.getAllProducts();
        Locale vietnamLocale = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);
        for (Product product : products) {
            product.setFormattedPrice(currencyFormatter.format(product.getPrice()));
        }

        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            model.addAttribute("message", "Giỏ hàng của bạn hiện tại trống.");
        } else {
            model.addAttribute("cart", cart);
        }
        return "/Cart/Cart";
    }

    //view classification
    @GetMapping("/AINhanDien")
    public String AINhanDien(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với AI Nhận Diện Gạo!");
        return "AIPrice/AINhanDien";
    }

}
