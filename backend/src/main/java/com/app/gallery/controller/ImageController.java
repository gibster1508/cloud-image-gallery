package com.app.gallery.controller;

import com.app.gallery.dto.ImageMetadataDto;
import com.app.gallery.entity.ImageMetadata;
import com.app.gallery.entity.enums.GalleryType;
import com.app.gallery.service.ImageService;
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
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE}
)
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/images")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public Page<ImageMetadata> getAllImages(Pageable pageable,
                                            @RequestParam(value = "galleryType") @NonNull GalleryType galleryType) {
        return imageService.getAllImages(pageable, galleryType);
    }

    @GetMapping("/searchImages")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public Page<ImageMetadataDto> searchImages(@RequestParam String keyword,
                                               @RequestParam GalleryType galleryType,
                                               Pageable pageable) {
        return imageService.searchImagesByClassifier(keyword, galleryType, pageable);
    }

    @PostMapping(path = "/image/upload")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "images") @NonNull MultipartFile[] multipartFiles,
                                             @RequestParam(value = "galleryType") @NonNull GalleryType galleryType) {
        imageService.uploadImage(multipartFiles, galleryType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/image/delete")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public ResponseEntity<String> deleteFile(
            @RequestParam(value = "id") @NonNull UUID id,
            @RequestParam(value = "url") @NonNull String url) {
        imageService.deleteImage(new ImageMetadataDto(id, url, GalleryType.GLOBAL));
        return ResponseEntity.ok().build();
    }
}