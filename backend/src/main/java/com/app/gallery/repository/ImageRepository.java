package com.app.gallery.repository;

import com.app.gallery.entity.ImageMetadata;
import com.app.gallery.entity.enums.GalleryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageMetadata, UUID> {
  Page<ImageMetadata> findAllByIdInAndGalleryType(List<UUID> imageIds, GalleryType galleryType, Pageable pageable);

  Page<ImageMetadata> findAllByIdInAndGalleryTypeAndUserId(List<UUID> imageIds, GalleryType galleryType, UUID userId, Pageable pageable);

  Page<ImageMetadata> findAllByGalleryType(GalleryType galleryType, Pageable pageable);

  Page<ImageMetadata> findAllByGalleryTypeAndUserId(GalleryType galleryType, UUID userId, Pageable pageable);

  void deleteById(@NonNull UUID imageId);
}
