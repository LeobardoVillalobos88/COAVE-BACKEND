package com.coave.coave.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngresosMesResponse {
    private int mes;
    private int anio;
    private double totalIngresos;
    private double ingresosPorHora;
    private double ingresosPension;
    private int totalAccesosPorHora;
    private int totalAccesosPension;
    private Map<String, Double> ingresosPorDia; // Fecha -> Monto
}