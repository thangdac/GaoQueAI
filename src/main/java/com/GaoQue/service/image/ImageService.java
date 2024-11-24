package com.GaoQue.service.image;

import com.GaoQue.dto.ImageDto;
import com.GaoQue.exceptions.ResourceNotFoundException;
import com.GaoQue.model.Image;
import com.GaoQue.model.Product;
import com.GaoQue.repository.ImageRepository;
import com.GaoQue.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;

    // Đường dẫn nơi hình ảnh sẽ được lưu
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
            throw new ResourceNotFoundException("No image found with id: " + id);
        });
    }

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);
        List<ImageDto> imageDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                Image image = new Image();
                image.setFileName(fileName);
                image.setFileType(file.getContentType());
                image.setDownloadUrl("/images/" + fileName);

                // Liên kết với sản phẩm
                image.setProduct(product);

                // Lưu vào cơ sở dữ liệu
                imageRepository.save(image);

                // Tạo ImageDto và thêm vào danh sách trả về
                ImageDto imageDto = new ImageDto();
                imageDto.setId(image.getId());
                imageDto.setFileName(image.getFileName());
                imageDto.setDownloadUrl(image.getDownloadUrl());
                imageDtos.add(imageDto);

            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu hình ảnh: " + e.getMessage());
            }
        }
        return imageDtos;
    }

    @Override
    public void updateImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Sản phẩm không tồn tại với ID: " + productId);
        }

        for (MultipartFile file : files) {
            try {
                // Tìm ảnh đã tồn tại theo tên file
                Image existingImage = imageRepository.findByProductAndFileName(product, file.getOriginalFilename());

                if (existingImage != null) {
                    // Nếu ảnh đã tồn tại, cập nhật ảnh mới
                    existingImage.setFileName(file.getOriginalFilename());
                    existingImage.setFileType(file.getContentType());
                    existingImage.setImage(new SerialBlob(file.getBytes()));
                    existingImage.setDownloadUrl("/images/" + file.getOriginalFilename());

                    // Lưu ảnh mới vào cơ sở dữ liệu
                    imageRepository.save(existingImage);
                } else {
                    // Nếu không có ảnh cũ, tạo ảnh mới
                    Image newImage = new Image();
                    newImage.setFileName(file.getOriginalFilename());
                    newImage.setFileType(file.getContentType());
                    newImage.setProduct(product);
                    newImage.setImage(new SerialBlob(file.getBytes()));
                    newImage.setDownloadUrl("/images/" + file.getOriginalFilename());

                    // Lưu ảnh mới vào cơ sở dữ liệu
                    imageRepository.save(newImage);
                }
                // Lưu ảnh vào thư mục hệ thống (src/main/resources/static/images/)
                String fileName = file.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.createDirectories(path.getParent());  // Tạo thư mục nếu chưa có
                Files.write(path, file.getBytes());  // Lưu ảnh vào thư mục

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Lỗi khi cập nhật hình ảnh: " + e.getMessage());
            }
        }
    }

}
