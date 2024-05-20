package com.example.demo.service;

import com.example.demo.dto.ImageMetadataDto;
import com.example.demo.entity.ImageMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
  Page<ImageMetadata> getAllImages(Pageable pageable);

  Page<ImageMetadataDto> searchImagesByClassifier(String keyword, Pageable pageable);

  void uploadImage(MultipartFile[] multipartFiles);

  void deleteImage(ImageMetadataDto image);
}
