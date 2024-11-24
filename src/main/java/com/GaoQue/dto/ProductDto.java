package com.GaoQue.dto;

import com.GaoQue.model.Category;
import com.GaoQue.model.Image;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String formattedPrice;
    private int inventory;
    private String description;
    private Category category;
    private List<ImageDto> images;
}


