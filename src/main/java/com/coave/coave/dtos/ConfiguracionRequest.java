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
public class ConfiguracionRequest {

    @NotNull(message = "La capacidad total es obligatoria")
    @Min(value = 1, message = "La capacidad total debe ser mayor a 0")
    private Integer capacidadTotal;

    @NotNull(message = "La capacidad por hora es obligatoria")
    @Min(value = 0, message = "La capacidad por hora debe ser mayor o igual a 0")
    private Integer capacidadPorHora;

    @NotNull(message = "La capacidad de pensión es obligatoria")
    @Min(value = 0, message = "La capacidad de pensión debe ser mayor o igual a 0")
    private Integer capacidadPension;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Min(value = 0, message = "La tarifa por hora debe ser mayor o igual a 0")
    private Double tarifaPorHora;

    @NotNull(message = "La tarifa de pensión mensual es obligatoria")
    @Min(value = 0, message = "La tarifa de pensión mensual debe ser mayor o igual a 0")
    private Double tarifaPensionMensual;

    @NotNull(message = "La expiración del QR es obligatoria")
    @Min(value = 1, message = "La expiración del QR debe ser mayor a 0")
    private Integer qrExpiracionMinutos;
}