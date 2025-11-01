package com.coave.coave.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratarPensionRequest {

    @NotBlank(message = "El ID del vehículo es obligatorio")
    private String vehiculoId;

    private String notas;
}