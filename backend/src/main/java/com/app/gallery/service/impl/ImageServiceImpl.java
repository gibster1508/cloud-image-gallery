package com.app.gallery.service.impl;

import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.app.gallery.config.properties.BucketProperties;
import com.app.gallery.dto.ImageMetadataDto;
import com.app.gallery.entity.ImageLabels;
import com.app.gallery.entity.ImageMetadata;
import com.app.gallery.entity.enums.GalleryType;
import com.app.gallery.exception.EntityNotFoundException;
import com.app.gallery.exception.ServiceException;
import com.app.gallery.exception.UnsupportedExtensionException;
import com.app.gallery.repository.ImageRepository;
import com.app.gallery.repository.LabelRepository;
import com.app.gallery.service.ImageService;
import com.app.gallery.service.LabelDetectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final AmazonS3 s3Client;
  private final BucketProperties bucketProperties;
  private final LabelDetectionService labelDetectionService;
  private final ImageRepository imageRepository;
  private final LabelRepository labelRepository;
  private final ObjectMapper objectMapper;

  @Override
  public Page<ImageMetadata> getAllImages(Pageable pageable, GalleryType galleryType) {
    if (GalleryType.GLOBAL.equals(galleryType)) {
      return imageRepository.findAllByGalleryType(galleryType, pageable);
    }
    UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    return imageRepository.findAllByGalleryTypeAndUserId(galleryType, userId, pageable);
  }

  @Override
  public Page<ImageMetadataDto> searchImagesByClassifier(String keyword, GalleryType galleryType, Pageable pageable) {
    List<ImageLabels> labels = labelRepository.findAllByLabelContains(keyword).orElseThrow(EntityNotFoundException::new);
    if (GalleryType.GLOBAL.equals(galleryType)) {
      return imageRepository
              .findAllByIdInAndGalleryType(labels.stream()
                      .map(label -> label.getImageMetadata().getId())
                      .collect(Collectors.toList()), galleryType, pageable)
              .map(image -> objectMapper.convertValue(image, ImageMetadataDto.class));
    }
    UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    return imageRepository
            .findAllByIdInAndGalleryTypeAndUserId(labels.stream()
                    .map(label -> label.getImageMetadata().getId())
                    .collect(Collectors.toList()), galleryType, userId, pageable)
            .map(image -> objectMapper.convertValue(image, ImageMetadataDto.class));
  }

  @Override
  public void uploadImage(MultipartFile[] multipartFiles, GalleryType galleryType) {
    for (MultipartFile multipartFile : multipartFiles) {
      try {
        validateAndUploadImage(multipartFile, galleryType);
      } catch (IOException e) {
        handleUploadError(multipartFile, e);
      }
    }
    log.info("Finished uploading files");
  }

  @Override
  @Transactional
  public void deleteImage(ImageMetadataDto imageMetadataDto) {
    UUID imageId = imageMetadataDto.getId();
    labelRepository.deleteByImageMetadataId(imageId);
    imageRepository.deleteById(imageId);
    log.info("Finished deleting image");
  }

  private void validateAndUploadImage(MultipartFile multipartFile, GalleryType galleryType) throws IOException {
    validateFileExtension(multipartFile);

    try (InputStream inputStream = multipartFile.getInputStream()) {
      ObjectMetadata meta = new ObjectMetadata();
      meta.setContentLength(multipartFile.getBytes().length);

      String originalFilename = multipartFile.getOriginalFilename();
      String s3Key = String.valueOf(originalFilename);

      s3Client.putObject(new PutObjectRequest(bucketProperties.getName(), s3Key, inputStream, meta));

      List<Label> labels = labelDetectionService.detectLabelsForImage(s3Key);
      AmazonS3URI uri = new AmazonS3URI(String.valueOf(s3Client.getUrl(bucketProperties.getName(), s3Key)));

      ImageMetadata createdImageMetadata = saveImageMetadata(uri.toString(), galleryType);
      List<ImageLabels> imageLabelsList = createImageLabelsList(labels, createdImageMetadata);

      labelRepository.saveAll(imageLabelsList);
    }
  }

  private void handleUploadError(MultipartFile multipartFile, IOException e) {
    log.error("Failed to upload file: {}", multipartFile.getOriginalFilename(), e);
    throw new ServiceException("Failed to upload the file");
  }

  private ImageMetadata saveImageMetadata(String imageUrl, GalleryType galleryType) {
    UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    ImageMetadata imageMetadata = ImageMetadata.builder()
            .url(imageUrl)
            .galleryType(galleryType)
            .userId(userId)
            .build();
    return imageRepository.save(imageMetadata);
  }

  private List<ImageLabels> createImageLabelsList(List<Label> labels, ImageMetadata createdImageMetadata) {
    return labels.stream()
            .map(label -> ImageLabels.builder()
                    .imageMetadata(createdImageMetadata)
                    .label(label.getName().toLowerCase(Locale.ROOT))
                    .build())
            .collect(Collectors.toList());
  }

  private static void validateFileExtension(MultipartFile multipartFile) {
    String originalFilename = multipartFile.getOriginalFilename();
    if (originalFilename == null || !isAllowedExtension(originalFilename)) {
      throw new UnsupportedExtensionException();
    }
  }

  private static boolean isAllowedExtension(String filename) {
    String[] allowedExtension = {"jpg", "png", "jpeg"};
    String lowerCaseFilename = filename.toLowerCase();
    for (String extension : allowedExtension) {
      if (lowerCaseFilename.endsWith("." + extension)) {
        return true;
      }
    }
    return false;
  }
}
