package com.coave.coave.dtos;

import com.coave.coave.models.enums.Modalidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerarQRRequest {

    @NotBlank(message = "El ID del vehículo es obligatorio")
    private String vehiculoId;

    @NotNull(message = "La modalidad es obligatoria")
    private Modalidad modalidad;
}