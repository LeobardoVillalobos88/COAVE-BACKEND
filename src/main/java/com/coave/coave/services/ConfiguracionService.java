package com.coave.coave.services;

import com.coave.coave.dtos.ConfiguracionRequest;
import com.coave.coave.models.Configuracion;
import com.coave.coave.repositories.ConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    @Value("${parking.capacidad.total}")
    private Integer capacidadTotalDefault;

    @Value("${parking.capacidad.por-hora}")
    private Integer capacidadPorHoraDefault;

    @Value("${parking.capacidad.pension}")
    private Integer capacidadPensionDefault;

    @Value("${parking.tarifa.por-hora}")
    private Double tarifaPorHoraDefault;

    @Value("${parking.tarifa.pension-mensual}")
    private Double tarifaPensionMensualDefault;

    @Value("${qr.expiracion-minutos}")
    private Integer qrExpiracionMinutosDefault;

    public Configuracion obtenerConfiguracion() {
        return configuracionRepository.findFirstByOrderByFechaActualizacionDesc()
                .orElseGet(this::crearConfiguracionPorDefecto);
    }

    private Configuracion crearConfiguracionPorDefecto() {
        Configuracion config = Configuracion.builder()
                .capacidadTotal(capacidadTotalDefault)
                .capacidadPorHora(capacidadPorHoraDefault)
                .capacidadPension(capacidadPensionDefault)
                .tarifaPorHora(tarifaPorHoraDefault)
                .tarifaPensionMensual(tarifaPensionMensualDefault)
                .qrExpiracionMinutos(qrExpiracionMinutosDefault)
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return configuracionRepository.save(config);
    }

    public Configuracion actualizar(ConfiguracionRequest request) {
        if (request.getCapacidadPorHora() + request.getCapacidadPension() != request.getCapacidadTotal()) {
            throw new RuntimeException("La suma de capacidades por hora y pensión debe ser igual a la capacidad total");
        }

        Configuracion config = Configuracion.builder()
                .capacidadTotal(request.getCapacidadTotal())
                .capacidadPorHora(request.getCapacidadPorHora())
                .capacidadPension(request.getCapacidadPension())
                .tarifaPorHora(request.getTarifaPorHora())
                .tarifaPensionMensual(request.getTarifaPensionMensual())
                .qrExpiracionMinutos(request.getQrExpiracionMinutos())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return configuracionRepository.save(config);
    }
}