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

import java.util.UUID;


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
    public Page<ImageMetadata> getAllImages(Pageable pageable) {
        return imageService.getAllImages(pageable);
    }

    @GetMapping("/searchImages")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public Page<ImageMetadataDto> searchImages(@RequestParam String keyword, Pageable pageable) {
        return imageService.searchImagesByClassifier(keyword, pageable);
    }

    @PostMapping(path = "/image/upload")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "images") @NonNull MultipartFile[] multipartFiles) {
        imageService.uploadImage(multipartFiles);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/image/delete")
    public ResponseEntity<String> deleteFile(
            @RequestParam(value = "id") @NonNull UUID id,
            @RequestParam(value = "url") @NonNull String url) {
        imageService.deleteImage(new ImageMetadataDto(id, url));
        return ResponseEntity.ok().build();
    }
}