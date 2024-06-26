package com.app.gallery.service;

import com.amazonaws.services.rekognition.model.Label;

import java.util.List;

public interface LabelDetectionService {
  List<Label> detectLabelsForImage(String fileName);
}
