package com.GaoQue.repository;

import com.GaoQue.model.Image;
import com.GaoQue.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long id);

    // Tìm hình ảnh theo sản phẩm và tên tệp
    Image findByProductAndFileName(Product product, String fileName);
}