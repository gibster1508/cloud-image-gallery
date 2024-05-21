package com.app.gallery.service;

import com.app.gallery.entity.ImageMetadata;
import com.app.gallery.dto.ImageMetadataDto;
import com.app.gallery.entity.enums.GalleryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  Page<ImageMetadata> getAllImages(Pageable pageable, GalleryType galleryType);

  Page<ImageMetadataDto> searchImagesByClassifier(String keyword, GalleryType galleryType, Pageable pageable);

  void uploadImage(MultipartFile[] multipartFiles, GalleryType galleryType);

  void deleteImage(ImageMetadataDto image);
}
