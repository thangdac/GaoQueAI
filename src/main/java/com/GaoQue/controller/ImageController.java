package com.GaoQue.controller;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Image;
import com.GaoQue.model.Product;
import com.GaoQue.request.ProductUpdateRequest;
import com.GaoQue.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    // Đặt hình ảnh trong thư mục resources/static/images
    private final String imageDir = "src/main/resources/static/images/";

    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
            // Đọc tệp hình ảnh từ thư mục
            Path path = Paths.get(imageDir + imageName);
            byte[] imageBytes = Files.readAllBytes(path);

            // Trả về hình ảnh với MIME type tương ứng (trong trường hợp này là JPEG)
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Có thể thay đổi MediaType tùy vào loại hình ảnh
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                    .body(imageBytes);
        } catch (IOException e) {
            // Trả về lỗi 404 nếu không tìm thấy hình ảnh
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint để cập nhật hình ảnh theo imageId
    @PostMapping("/images/{imageId}")
    public ResponseEntity<String> updateImage(@PathVariable Long imageId,
                                              @RequestParam List<MultipartFile> files) {
        try {
            // Gọi ImageService để cập nhật ảnh theo imageId
            imageService.updateImages(imageId, files);
            return ResponseEntity.ok("Cập nhật hình ảnh thành công!");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy hình ảnh với ID: " + imageId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi cập nhật hình ảnh: " + e.getMessage());
        }
    }
}
