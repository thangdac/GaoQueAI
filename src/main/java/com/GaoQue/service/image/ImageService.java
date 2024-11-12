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
import java.io.File;
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

    // Đường dẫn nơi hình ảnh sẽ được lưu
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);
        List<ImageDto> imageDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // Lưu ảnh vào thư mục cố định ngoài src/main/resources
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent()); // Tạo thư mục nếu chưa có
                Files.write(path, file.getBytes());

                // Tạo đối tượng Image và lưu vào cơ sở dữ liệu
                Image image = new Image();
                image.setFileName(fileName);
                image.setFileType(file.getContentType());
                image.setDownloadUrl("/images/" + fileName);  // Đường dẫn để tải hình ảnh

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
        Product product = productService.getProductById(productId);  // Lấy sản phẩm theo ID

        for (MultipartFile file : files) {
            try {
                // Tìm hình ảnh hiện có theo tên file và sản phẩm
                Image image = imageRepository.findByProductAndFileName(product, file.getOriginalFilename());

                if (image != null) {
                    // Xóa ảnh cũ nếu có và cập nhật lại thông tin
                    deleteAllImagesByProductId(productId);  // Gọi hàm xóa ảnh cũ từ cơ sở dữ liệu và hệ thống tệp

                    // Cập nhật thông tin của hình ảnh
                    image.setFileName(file.getOriginalFilename());
                    image.setFileType(file.getContentType());
                    image.setImage(new SerialBlob(file.getBytes()));
                    image.setDownloadUrl("/images/" + file.getOriginalFilename());

                    // Lưu lại hình ảnh mới vào cơ sở dữ liệu
                    imageRepository.save(image);
                } else {
                    // Tạo hình ảnh mới nếu chưa tồn tại
                    image = new Image();
                    image.setFileName(file.getOriginalFilename());
                    image.setFileType(file.getContentType());
                    image.setProduct(product);
                    image.setImage(new SerialBlob(file.getBytes()));  // Lưu dữ liệu hình ảnh dưới dạng Blob
                    image.setDownloadUrl("/images/" + file.getOriginalFilename());
                    // Lưu ảnh mới vào cơ sở dữ liệu
                    imageRepository.save(image);
                }
                // Lưu ảnh vào thư mục trên hệ thống
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());  // Tạo thư mục nếu chưa có
                Files.write(path, file.getBytes());  // Lưu ảnh vào thư mục

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Lỗi khi cập nhật hình ảnh: " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteAllImagesByProductId(Long productId) {
        // Lấy sản phẩm theo ID
        Product product = productService.getProductById(productId);

        // Tìm tất cả các hình ảnh liên quan đến sản phẩm
        List<Image> images = imageRepository.findByProductId(productId);

        if (images.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy ảnh nào cho sản phẩm với ID: " + productId);
        }

        // Xóa các ảnh khỏi cơ sở dữ liệu và hệ thống tệp
        for (Image image : images) {
            // Xóa ảnh khỏi cơ sở dữ liệu
            imageRepository.delete(image);

            // Xóa ảnh khỏi hệ thống tệp
            Path imagePath = Paths.get(UPLOAD_DIR + image.getFileName());
            try {
                Files.deleteIfExists(imagePath);  // Xóa ảnh khỏi hệ thống tệp nếu tồn tại
                System.out.println("Đã xóa ảnh khỏi hệ thống tệp: " + imagePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Không thể xóa ảnh khỏi thư mục: " + e.getMessage());
            }
        }

        System.out.println("Đã xóa tất cả ảnh cho sản phẩm với ID: " + productId);
    }


}
