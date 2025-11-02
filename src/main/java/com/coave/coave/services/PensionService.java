package com.coave.coave.services;

import com.coave.coave.dtos.PensionRequest;
import com.coave.coave.exception.BadRequestException;
import com.coave.coave.exception.ResourceNotFoundException;
import com.coave.coave.models.Pension;
import com.coave.coave.models.enums.EstadoPension;
import com.coave.coave.repositories.ConfiguracionRepository;
import com.coave.coave.repositories.PensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PensionService {

    private final PensionRepository pensionRepository;
    private final ConfiguracionService configuracionService;

    public Pension crear(PensionRequest request) {
        pensionRepository.findByConductorIdAndEstado(request.getConductorId(), EstadoPension.ACTIVA)
                .ifPresent(p -> {
                    throw new BadRequestException("El conductor ya tiene una pensión activa");
                });

        Double monto = configuracionService.obtenerConfiguracion().getTarifaPensionMensual();

        Pension pension = Pension.builder()
                .conductorId(request.getConductorId())
                .vehiculoId(request.getVehiculoId())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaInicio().plusMonths(1))
                .monto(monto)
                .estado(EstadoPension.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .notas(request.getNotas())
                .build();

        return pensionRepository.save(pension);
    }

    public Pension renovar(String pensionId) {
        Pension pension = pensionRepository.findById(pensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pensión no encontrada"));

        // Si la pensión está cancelada, no se puede renovar
        if (pension.getEstado() == EstadoPension.CANCELADA) {
            throw new BadRequestException("No se puede renovar una pensión cancelada");
        }

        // Extender la fecha de fin por un mes más desde la fecha de fin actual
        // Esto preserva el tiempo ya pagado
        pension.setFechaFin(pension.getFechaFin().plusMonths(1));
        pension.setEstado(EstadoPension.ACTIVA);
        pension.setFechaRenovacion(LocalDateTime.now());

        return pensionRepository.save(pension);
    }

    public void cancelar(String pensionId) {
        Pension pension = pensionRepository.findById(pensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pensión no encontrada"));

        pension.setEstado(EstadoPension.CANCELADA);
        pensionRepository.save(pension);
    }

    public List<Pension> obtenerPorConductor(String conductorId) {
        return pensionRepository.findByConductorId(conductorId);
    }

    public List<Pension> obtenerTodas() {
        return pensionRepository.findAll();
    }

    public List<Pension> obtenerActivas() {
        return pensionRepository.findByEstado(EstadoPension.ACTIVA);
    }

    public List<Pension> obtenerPorEstado(EstadoPension estado) {
        return pensionRepository.findByEstado(estado);
    }

    public void actualizarPensionesVencidas() {
        List<Pension> pensionesVencidas = pensionRepository.findByFechaFinBefore(LocalDate.now());
        pensionesVencidas.forEach(pension -> {
            if (pension.getEstado() == EstadoPension.ACTIVA) {
                pension.setEstado(EstadoPension.VENCIDA);
                pensionRepository.save(pension);
            }
        });
    }
}