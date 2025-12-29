package com.licenta.biomechanics_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MetadataDTO {
    private Double detectedHeight;
    private Double stancePhase;
    private Double kneeFlexion;
    private Double cadence;
    private String scanType;
}
