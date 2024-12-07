package com.GaoQue.controller;

import com.GaoQue.service.AIPrice.RiceClassificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AINhanDienController {

    // Dịch vụ phân loại gạo được tiêm vào
    private final RiceClassificationService riceClassificationService;

    // Constructor để tiêm dịch vụ
    public AINhanDienController(RiceClassificationService riceClassificationService) {
        this.riceClassificationService = riceClassificationService;
    }

    private boolean BienTrangThai = true; // Biến lưu trạng thái

    @GetMapping("/Admin/AINhanDien")
    public String hienthi(Model model) {
        String trangThaiText = BienTrangThai ? "True" : "False";
        model.addAttribute("bienTrangThai", trangThaiText);
        return "/Admin/AI/AINhanDien";
    }

    @GetMapping("api/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        return ResponseEntity.ok(Map.of("bienTrangThai", BienTrangThai));
    }

    @PostMapping("/Admin/AINhanDien")
    public String change(RedirectAttributes redirectAttributes) {
        BienTrangThai = !BienTrangThai;
        redirectAttributes.addFlashAttribute("message", "Thay đổi trạng thái thành công!");
        return "redirect:/Admin/AINhanDien";
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
