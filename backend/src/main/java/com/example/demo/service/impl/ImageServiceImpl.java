package com.example.demo.service.impl;

import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.config.properties.BucketProperties;
import com.example.demo.dto.ImageMetadataDto;
import com.example.demo.entity.ImageLabels;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.exception.ServiceException;
import com.example.demo.exception.UnsupportedExtensionException;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.LabelRepository;
import com.example.demo.service.ImageService;
import com.example.demo.service.LabelDetectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
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
  public Page<ImageMetadata> getAllImages(Pageable pageable) {
    return imageRepository.findAll(pageable);
  }

  @Override
  public Page<ImageMetadataDto> searchImagesByClassifier(String keyword, Pageable pageable) {
    List<ImageLabels> labels = labelRepository.findAllByLabelContains(keyword).orElseThrow(EntityNotFoundException::new);
    return imageRepository
            .findAllByIdIn(labels.stream()
                    .map(label -> label.getImageMetadata().getId())
                    .collect(Collectors.toList()), pageable)
            .map(image -> objectMapper.convertValue(image, ImageMetadataDto.class));
  }

  @Override
  public void uploadImage(MultipartFile[] multipartFiles) {
    for (MultipartFile multipartFile : multipartFiles) {
      try {
        validateAndUploadImage(multipartFile);
      } catch (IOException e) {
        handleUploadError(multipartFile, e);
      }
    }
    log.info("Finished uploading files");
  }

  private void validateAndUploadImage(MultipartFile multipartFile) throws IOException {
    validateFileExtension(multipartFile);

    try (InputStream inputStream = multipartFile.getInputStream()) {
      ObjectMetadata meta = new ObjectMetadata();
      meta.setContentLength(multipartFile.getBytes().length);

      String originalFilename = multipartFile.getOriginalFilename();
      String s3Key = String.valueOf(originalFilename);

      s3Client.putObject(new PutObjectRequest(bucketProperties.getName(), s3Key, inputStream, meta));

      List<Label> labels = labelDetectionService.detectLabelsForImage(s3Key);
      AmazonS3URI uri = new AmazonS3URI(String.valueOf(s3Client.getUrl(bucketProperties.getName(), s3Key)));

      ImageMetadata createdImageMetadata = saveImageMetadata(uri.toString());
      List<ImageLabels> imageLabelsList = createImageLabelsList(labels, createdImageMetadata);

      labelRepository.saveAll(imageLabelsList);
    }
  }

  private void handleUploadError(MultipartFile multipartFile, IOException e) {
    log.error("Failed to upload file: {}", multipartFile.getOriginalFilename(), e);
    throw new ServiceException("Failed to upload the file");
  }

  private ImageMetadata saveImageMetadata(String imageUrl) {
    ImageMetadata imageMetadata = ImageMetadata.builder()
            .url(imageUrl)
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
