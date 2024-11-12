package com.GaoQue.service.image;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    Image getImageById(Long id);
    void deleteImageById(Long id);
    void deleteAllImagesByProductId(Long id);
    List<ImageDto> saveImages(Long productId, List<MultipartFile> files);
    void updateImages(Long productId, List<MultipartFile> files);
}
