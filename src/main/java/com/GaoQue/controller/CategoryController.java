package com.GaoQue.controller;

import com.GaoQue.dto.ProductDto;
import com.GaoQue.exceptions.AlreadyExistsException;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Category;
import com.GaoQue.model.Product;
import com.GaoQue.service.category.CategoryService;
import com.GaoQue.service.image.ImageService;
import com.GaoQue.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private ProductService productService;
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //List product
    @GetMapping("/Admin/Category")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "/Admin/Category/Category";
    }

    //add category
    @GetMapping("/Admin/AddCategory")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "/Admin/Category/AddCategory";
    }

    @PostMapping("/Admin/AddCategory")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            Category theCategory = categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Thêm loại sản phẩm thành công!");
            return "redirect:/Admin/Category";
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/Admin/Category/AddCategory";
        }
    }

    //update category
    @GetMapping("/Admin/UpdateCategory/{id}")
    public String showUpdateCategoryForm( @PathVariable Long id, Model model) {
        try {

        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "/Admin/Category/UpdateCategory";
        }catch (ResourceNotFoundException e){
            model.addAttribute("error", e.getMessage());
            return "/Admin/Pages/Page404";
        }
    }

    @PostMapping("/Admin/UpdateCategory/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            model.addAttribute("category", updatedCategory);
            redirectAttributes.addFlashAttribute("message", "Cập nhật loại sản phẩm thành công!");
            return "redirect:/Admin/Category";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "/Admin/Pages/Page404";
        }
    }

    //delete category
    @GetMapping("/Admin/DeleteCategory/{id}")
    public String deleteCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa loại sản phẩm thành công!");
            return "redirect:/Admin/Category";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/Admin/Category";
        }
    }




}
