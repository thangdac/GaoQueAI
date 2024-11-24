package com.GaoQue.controller;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Category;
import com.GaoQue.model.Image;
import com.GaoQue.model.Product;
import com.GaoQue.request.ProductUpdateRequest;
import com.GaoQue.service.image.ImageService;
import com.GaoQue.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;
    private ProductService productService;

    private final String imageDir = "src/main/resources/static/images/";

    //Lưu hình
    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
            Path path = Paths.get(imageDir + imageName);
            byte[] imageBytes = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
