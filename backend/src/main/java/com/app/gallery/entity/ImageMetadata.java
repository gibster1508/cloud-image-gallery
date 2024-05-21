package com.app.gallery.entity;

import com.app.gallery.entity.enums.GalleryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ImageMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String url;

    @OneToMany(mappedBy = "imageMetadata")
    private Set<ImageLabels> imageLabels;

    @Enumerated(EnumType.STRING)
    private GalleryType galleryType;
    private UUID userId;
}