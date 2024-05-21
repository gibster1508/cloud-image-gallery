package com.app.gallery.repository;

import com.app.gallery.entity.ImageLabels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<ImageLabels, UUID> {
  Optional<List<ImageLabels>> findAllByLabelContains(String label);

  void deleteByImageMetadataId(UUID imageId);
}
