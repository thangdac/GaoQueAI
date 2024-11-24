package com.GaoQue.controller;

import com.GaoQue.dto.ProductDto;
import com.GaoQue.model.Product;
import com.GaoQue.model.User;
import com.GaoQue.service.product.ProductService;
import com.GaoQue.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


@Controller
public class PageController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

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


    @GetMapping("/Home")
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với trang chủ!");
        return "Home/home";
    }

    @GetMapping("/Product")
    public String getAllProductsuser(Model model) {
        List<Product> products = productService.getAllProducts();
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);
        for (Product product : products) {
            product.setFormattedPrice(currencyFormatter.format(product.getPrice()));
        }
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        model.addAttribute("products", convertedProducts);
        return "/Product/product";
    }

    @GetMapping("/AINhanDien")
    public String AINhanDien(Model model) {
        model.addAttribute("message", "Chào mừng bạn đến với AI Nhận Diện Gạo!");
        return "AIPrice/AINhanDien";
    }
}
