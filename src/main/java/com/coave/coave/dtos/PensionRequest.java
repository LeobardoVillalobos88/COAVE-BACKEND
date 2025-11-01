package com.coave.coave.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionRequest {

    @NotBlank(message = "El ID del conductor es obligatorio")
    private String conductorId;

    @NotBlank(message = "El ID del vehículo es obligatorio")
    private String vehiculoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private String notas;
}

