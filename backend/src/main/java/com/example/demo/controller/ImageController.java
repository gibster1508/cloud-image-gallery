package com.example.demo.controller;

import com.example.demo.dto.ImageMetadataDto;
import com.example.demo.entity.ImageMetadata;
import com.example.demo.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST }
)
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/images")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public Page<ImageMetadata> getAllImages(Pageable pageable) {
        return imageService.getAllImages(pageable);
    }

    @GetMapping("/searchImages")
    public Page<ImageMetadataDto> searchImages(@RequestParam String keyword, Pageable pageable) {
        return imageService.searchImagesByClassifier(keyword, pageable);
    }

    @PostMapping(path = "/image/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "images") @NonNull MultipartFile[] multipartFiles) {
        imageService.uploadImage(multipartFiles);
        return ResponseEntity.ok().build();
    }
}