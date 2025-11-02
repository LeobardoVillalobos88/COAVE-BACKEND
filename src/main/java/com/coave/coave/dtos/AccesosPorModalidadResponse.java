package com.coave.coave.dtos;

import com.coave.coave.models.enums.Modalidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccesosPorModalidadResponse {
    private Modalidad modalidad;
    private long totalAccesos;
    private long accesosMesActual;
    private long vehiculosActivos;
    private double ingresosMesActual;
    private double promedioTiempoEstancia; // En horas
}