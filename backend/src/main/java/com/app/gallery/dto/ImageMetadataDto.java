package com.app.gallery.dto;

import com.app.gallery.entity.enums.GalleryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ImageMetadataDto {
    private UUID id;
    private String url;
    private GalleryType galleryType;
}
