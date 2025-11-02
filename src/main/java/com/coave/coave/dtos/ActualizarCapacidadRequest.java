package com.coave.coave.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCapacidadRequest {

    @NotNull(message = "La capacidad total es obligatoria")
    @Min(value = 1, message = "La capacidad total debe ser mayor a 0")
    private Integer capacidadTotal;

    @NotNull(message = "La capacidad por hora es obligatoria")
    @Min(value = 0, message = "La capacidad por hora debe ser mayor o igual a 0")
    private Integer capacidadPorHora;

    @NotNull(message = "La capacidad de pensión es obligatoria")
    @Min(value = 0, message = "La capacidad de pensión debe ser mayor o igual a 0")
    private Integer capacidadPension;
}
