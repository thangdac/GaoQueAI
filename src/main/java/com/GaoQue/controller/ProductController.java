package com.GaoQue.controller;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.dto.ProductDto;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Category;
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
import java.text.NumberFormat;
import java.util.Locale;


import java.util.List;
import java.util.stream.Collectors;

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

    //Admin
    //List product
    @GetMapping("/Admin/Product")
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        // Định dạng giá từng sản phẩm theo kiểu Việt Nam
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);

        for (Product product : products) {
            product.setFormattedPrice(currencyFormatter.format(product.getPrice()));
        }

        model.addAttribute("products", convertedProducts);


        return "/Admin/Product/Product";
    }

    //Detail Product
    @GetMapping("Admin/ProductDetail/{productId}")
    public ModelAndView getProductById(@PathVariable Long productId) {
        ModelAndView modelAndView = new ModelAndView("/Admin/Product/ProductDetail");
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            modelAndView.addObject("product", productDto);
            modelAndView.setViewName("/Admin/Product/ProductDetail");
        } catch (ResourceNotFoundException e) {
            modelAndView.setViewName("/Admin/Pages/Page404");
            modelAndView.addObject("errorMessage", e.getMessage());
        } catch (Exception e) {
            modelAndView.setViewName("/Admin/Pages/Page404");
            modelAndView.addObject("errorMessage", "Internal server error");
        }
        return modelAndView;
    }

    //add product
    @GetMapping("/Admin/AddProduct")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new AddProductRequest());
        return "/Admin/Product/AddProduct";
    }

    @PostMapping("Admin/AddProduct")
    public String addProduct(@ModelAttribute("product") AddProductRequest product,
                             @RequestParam List<MultipartFile> files,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/Admin/Product/AddProduct"; // Nếu có lỗi validation, trả về lại trang form.
        }
        try {
            Product theProduct = productService.addProduct(product);
            List<ImageDto> imageDtos = imageService.saveImages(theProduct.getId(), files);
            ProductDto productDto = productService.convertToDto(theProduct);
            model.addAttribute("message", "Thêm sản phẩm thành công!");
            model.addAttribute("product", productDto);
            model.addAttribute("images", imageDtos);
            return "/Admin/Product/ProductDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add product: " + e.getMessage());
            return "/Admin/Product/AddProduct";
        }
    }

    // Update Product
    @GetMapping("Admin/UpdateProduct/{productId}")
    public String updateProductForm(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("product", product);
            model.addAttribute("categories", categories);
            return "Admin/Product/UpdateProduct";
        } catch (ResourceNotFoundException e) {
            return "Admin/Pages/Page404";
        }
    }

    @PostMapping("Admin/UpdateProduct/{productId}")
    public String updateProduct(@PathVariable Long productId,
                                @ModelAttribute ProductUpdateRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/Admin/UpdateProduct/{productId}";
        }
        try {
            Product updatedProduct = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(updatedProduct);
            redirectAttributes.addFlashAttribute("product", productDto);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
            return "redirect:/Admin/Product";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/Admin/Product";
        }
    }

    //Delete Product
    @GetMapping("Admin/DeleteProduct/{productId}")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes, Model model) {
        try {
            productService.deleteProductById(productId);
            model.addAttribute("message", "Xóa sản phẩm thành công!");
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/Admin/Pages/Page404";
        }
        return "redirect:/Admin/Product";
    }

    // Update Image
    @GetMapping("Admin/UpdateImage/{productId}")
    public String updateImageForm(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            model.addAttribute("product", product);
            return "Admin/Product/UpdateImage";
        } catch (ResourceNotFoundException e) {
            return "Admin/Pages/Page404";
        }
    }

    @PostMapping("/Admin/UpdateImage/{productId}")
    public String uploadImages(@PathVariable Long productId,
                               @RequestParam("files") List<MultipartFile> files,
                               Model model) {
        Product product = productService.getProductById(productId);
        if (files.isEmpty()) {
            model.addAttribute("error", "Không có file nào được chọn.");
            model.addAttribute("product", product);
            return "Admin/Product/UpdateImage";
        }
        List<String> errorMessages = files.stream()
                .filter(file -> !file.getContentType().startsWith("image") || file.getSize() > 5 * 1024 * 1024)
                .map(file -> "File " + file.getOriginalFilename() + " không hợp lệ.")
                .collect(Collectors.toList());

        if (!errorMessages.isEmpty()) {
            model.addAttribute("error", String.join("<br/>", errorMessages));
            model.addAttribute("product", product);
            return "Admin/Product/UpdateImage";
        }
        try {
            List<ImageDto> uploadedImages = imageService.saveImages(productId, files);
            model.addAttribute("uploadedImages", uploadedImages);
            model.addAttribute("message", "Tải hình ảnh lên thành công!");
            model.addAttribute("product", product);
            return "redirect:/Admin/UpdateImage/" + productId; // Correct redirect
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("product", product);
            return "Admin/Product/UpdateImage";
        }
    }

    //delete Image
    @PostMapping("/Admin/DeleteImage/{imageId}")
    public String deleteImage(@PathVariable Long imageId, @RequestParam Long productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            imageService.deleteImageById(imageId);
            model.addAttribute("message", "Xóa ảnh thành công!");
            Product product = productService.getProductById(productId);
            model.addAttribute("product", product);
            redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm!");
            return "redirect:/Admin/UpdateImage/" + productId;
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Không tìm thấy ảnh với ID: " + imageId);
            return "Admin/Pages/Page404";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa ảnh: " + e.getMessage());
            return "Admin/Product/UpdateImage";
        }
    }

}
