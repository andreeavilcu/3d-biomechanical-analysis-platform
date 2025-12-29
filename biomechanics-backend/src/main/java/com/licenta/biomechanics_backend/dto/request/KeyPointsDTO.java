package com.licenta.biomechanics_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeyPointsDTO {
    @Valid
    @NotNull(message = "Left ear keypoint is required")
    @JsonProperty("l_ear")
    private Point3DDTO leftEar;

    @Valid
    @NotNull(message = "Left shoulder keypoint is required")
    @JsonProperty("l_shoulder")
    private Point3DDTO leftShoulder;

    @Valid
    @NotNull(message = "Right shoulder keypoint is required")
    @JsonProperty("r_shoulder")
    private Point3DDTO rightShoulder;

    @Valid
    @NotNull(message = "Left hip keypoint is required")
    @JsonProperty("l_hip")
    private Point3DDTO leftHip;

    @Valid
    @NotNull(message = "Left knee keypoint is required")
    @JsonProperty("l_knee")
    private Point3DDTO leftKnee;

    @Valid
    @NotNull(message = "Left ankle keypoint is required")
    @JsonProperty("l_ankle")
    private Point3DDTO leftAnkle;

    @Valid
    private MetadataDTO meta;


}
