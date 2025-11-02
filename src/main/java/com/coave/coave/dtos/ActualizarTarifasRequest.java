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
public class ActualizarTarifasRequest {

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Min(value = 0, message = "La tarifa por hora debe ser mayor o igual a 0")
    private Double tarifaPorHora;

    @NotNull(message = "La tarifa de pensión mensual es obligatoria")
    @Min(value = 0, message = "La tarifa de pensión mensual debe ser mayor o igual a 0")
    private Double tarifaPensionMensual;
}
