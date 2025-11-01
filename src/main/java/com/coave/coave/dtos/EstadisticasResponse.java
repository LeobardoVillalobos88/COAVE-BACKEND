package com.coave.coave.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasResponse {

    private Integer capacidadTotal;

    private Integer capacidadPorHora;

    private Integer capacidadPension;

    private Integer vehiculosActualesPorHora;

    private Integer vehiculosActualesPension;

    private Integer espaciosDisponiblesPorHora;

    private Integer espaciosDisponiblesPension;

    private Integer totalVehiculosRegistrados;

    private Integer totalConductores;

    private Integer pensionesActivas;

    private Double ingresosTotalesMes;

    private Double ingresosPorHoraMes;

    private Double ingresosPensionMes;
}