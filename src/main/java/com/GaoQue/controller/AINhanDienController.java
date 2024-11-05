package com.GaoQue.controller;

import com.GaoQue.service.AIPrice.RiceClassificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AINhanDienController {

    // Dịch vụ phân loại gạo được tiêm vào
    private final RiceClassificationService riceClassificationService;

    // Constructor để tiêm dịch vụ
    public AINhanDienController(RiceClassificationService riceClassificationService) {
        this.riceClassificationService = riceClassificationService;
    }

    // Phương thức nhận ảnh từ view
    @PostMapping("/classify")
    public ResponseEntity<?> classifyRice(@RequestParam("image") MultipartFile file) {
        try {
            String riceType = riceClassificationService.classifyRice(file);
            return ResponseEntity.ok().body(Map.of("riceType", riceType));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Có lỗi xảy ra trong quá trình nhận diện.");
        }
    }
}
