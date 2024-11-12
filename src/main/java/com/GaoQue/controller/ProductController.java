package com.GaoQue.controller;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.dto.ProductDto;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Category;
import com.GaoQue.model.Image;
import com.GaoQue.model.Product;
import com.GaoQue.request.AddProductRequest;
import com.GaoQue.request.ProductUpdateRequest;
import com.GaoQue.service.category.CategoryService;
import com.GaoQue.service.image.ImageService;
import com.GaoQue.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    private CategoryService categoryService;
    private ImageService imageService;

    @Autowired
    public ProductController(CategoryService categoryService, ImageService imageService) {
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    @GetMapping("/Admin/AdminProduct")
    public String getAllProducts(Model model) {
        // Lấy danh sách sản phẩm từ service
        List<Product> products = productService.getAllProducts();
        // Chuyển đổi danh sách sản phẩm sang dạng ProductDto
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        // Thêm danh sách sản phẩm vào model
        model.addAttribute("products", convertedProducts);
        // Trả về tên của View (file HTML) sẽ được render
        return "/Admin/Manage/Product";
    }

    //chi tiết sản phẩm
    @GetMapping("Admin/ProductDetail/{productId}")
    public ModelAndView getProductById(@PathVariable Long productId) {
        ModelAndView modelAndView = new ModelAndView("/Admin/Manage/ProductDetail"); // Tên của view (HTML) bạn muốn trả về
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            modelAndView.addObject("product", productDto); // Thêm sản phẩm vào model
            modelAndView.setViewName("/Admin/Manage/ProductDetail"); // Chỉ định view name
        } catch (ResourceNotFoundException e) {
            modelAndView.setViewName("/Admin/Pages/Page404"); // Chỉ định view lỗi
            modelAndView.addObject("errorMessage", e.getMessage()); // Thêm thông điệp lỗi vào model
        } catch (Exception e) {
            modelAndView.setViewName("/Admin/Pages/Page404");
            modelAndView.addObject("errorMessage", "Internal server error");
        }
        return modelAndView; // Trả về model và view
    }

    //add product
    @GetMapping("/Admin/AddProduct")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        // Khởi tạo đối tượng product cho form
        model.addAttribute("product", new AddProductRequest());
        return "/Admin/Manage/AddProduct";
    }

    @PostMapping("Admin/AddProduct")
    public String addProduct(@ModelAttribute("product") AddProductRequest product,
                             @RequestParam List<MultipartFile> files,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/Admin/Manage/AddProduct"; // Nếu có lỗi validation, trả về lại trang form.
        }
        try {
            // Thêm sản phẩm vào hệ thống
            Product theProduct = productService.addProduct(product);
            // Lưu hình ảnh cho sản phẩm
            List<ImageDto> imageDtos = imageService.saveImages(theProduct.getId(), files);
            // Chuyển sản phẩm sang DTO để dễ hiển thị trên giao diện
            ProductDto productDto = productService.convertToDto(theProduct);
            // Thêm thông tin vào model để hiển thị trong view
            model.addAttribute("message", "Thêm sản phẩm thành công!");
            model.addAttribute("product", productDto);
            model.addAttribute("images", imageDtos); // Danh sách hình ảnh
            // Điều hướng đến trang hiển thị chi tiết sản phẩm (ví dụ: "product-detail.html")
            return "/Admin/Manage/ProductDetail";
        } catch (Exception e) {
            // Thêm thông báo lỗi vào model
            model.addAttribute("error", "Failed to add product: " + e.getMessage());
            // Điều hướng đến trang lỗi hoặc trang thêm sản phẩm
            return "/Admin/Manage/AddProduct";
        }
    }

    //xóa sản phẩm
    @GetMapping("Admin/DeleteProduct/{productId}")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            // Thêm thông báo thành công vào flash attributes để hiển thị trên giao diện
            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
        } catch (ResourceNotFoundException e) {
            // Thêm thông báo lỗi vào flash attributes
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        // Chuyển hướng về trang danh sách sản phẩm
        return "redirect:/Admin/AdminProduct";
    }

    @GetMapping("Admin/UpdateProduct/{productId}")
    public String UpdateProduct(@PathVariable Long productId, Model model) {
        try {
            // Lấy sản phẩm từ database
            Product product = productService.getProductById(productId);
            List<Category> categories = categoryService.getAllCategories();

            model.addAttribute("product", product);
            model.addAttribute("categories", categories);

            return "Admin/Manage/UpdateProduct";
        } catch (ResourceNotFoundException e) {
            return "Admin/Pages/Page404";
        }
    }

    @PostMapping("Admin/UpdateProduct/{productId}")
    public String updateProduct(@PathVariable Long productId, @ModelAttribute ProductUpdateRequest request, Model model,
                                @RequestParam List<MultipartFile> files) {
        try {
            // Gọi phương thức updateProduct trong service để cập nhật sản phẩm
            Product updatedProduct = productService.updateProduct(request, productId);

            // Cập nhật hình ảnh liên quan đến sản phẩm
            imageService.updateImages( updatedProduct.getId(), files);

            // Thêm thông tin sản phẩm đã cập nhật vào model
            model.addAttribute("product", updatedProduct);
            // Thêm thông báo thành công
            model.addAttribute("message", "Cập nhật sản phẩm thành công!");
            return "Admin/Manage/ProductDetail";  // Trả về trang cập nhật sản phẩm với thông báo thành công
        } catch (ResourceNotFoundException e) {
            return "Admin/Pages/Page404";
        }
    }
}
