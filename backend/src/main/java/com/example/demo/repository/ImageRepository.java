package com.example.demo.repository;

import com.example.demo.entity.ImageMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageMetadata, UUID> {
  Page<ImageMetadata> findAllByIdIn(List<UUID> imageIds, Pageable pageable);
}
