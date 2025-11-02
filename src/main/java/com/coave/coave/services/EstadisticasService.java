package com.coave.coave.services;

import com.coave.coave.dtos.AccesosPorModalidadResponse;
import com.coave.coave.dtos.EstadisticasResponse;
import com.coave.coave.dtos.IngresosMesResponse;
import com.coave.coave.models.Configuracion;
import com.coave.coave.models.RegistroAcceso;
import com.coave.coave.models.enums.EstadoPension;
import com.coave.coave.models.enums.EstadoRegistro;
import com.coave.coave.models.enums.Modalidad;
import com.coave.coave.models.enums.Rol;
import com.coave.coave.repositories.PensionRepository;
import com.coave.coave.repositories.RegistroAccesoRepository;
import com.coave.coave.repositories.UsuarioRepository;
import com.coave.coave.repositories.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ConfiguracionService configuracionService;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PensionRepository pensionRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final AccesoService accesoService;

    public EstadisticasResponse obtenerEstadisticas() {
        Configuracion config = configuracionService.obtenerConfiguracion();

        long vehiculosActualesPorHora = contarVehiculosActualesPorModalidad(Modalidad.POR_HORA);
        long vehiculosActualesPension = contarVehiculosActualesPorModalidad(Modalidad.PENSION);

        long totalVehiculos = vehiculoRepository.count();
        long totalConductores = usuarioRepository.findByRol(Rol.CONDUCTOR).size();
        long pensionesActivas = pensionRepository.countByEstado(EstadoPension.ACTIVA);

        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        List<RegistroAcceso> registrosMes = registroAccesoRepository.findByFechaHoraBetween(inicioMes, finMes);

        double ingresosPorHora = registrosMes.stream()
                .filter(r -> r.getModalidad() == Modalidad.POR_HORA && r.getMontoCalculado() != null)
                .mapToDouble(RegistroAcceso::getMontoCalculado)
                .sum();

        double ingresosPension = pensionesActivas * config.getTarifaPensionMensual();
        double ingresosTotales = ingresosPorHora + ingresosPension;

        return EstadisticasResponse.builder()
                .capacidadTotal(config.getCapacidadTotal())
                .capacidadPorHora(config.getCapacidadPorHora())
                .capacidadPension(config.getCapacidadPension())
                .vehiculosActualesPorHora((int) vehiculosActualesPorHora)
                .vehiculosActualesPension((int) vehiculosActualesPension)
                .espaciosDisponiblesPorHora(config.getCapacidadPorHora() - (int) vehiculosActualesPorHora)
                .espaciosDisponiblesPension(config.getCapacidadPension() - (int) vehiculosActualesPension)
                .totalVehiculosRegistrados((int) totalVehiculos)
                .totalConductores((int) totalConductores)
                .pensionesActivas((int) pensionesActivas)
                .ingresosTotalesMes(ingresosTotales)
                .ingresosPorHoraMes(ingresosPorHora)
                .ingresosPensionMes(ingresosPension)
                .build();
    }

    public IngresosMesResponse obtenerIngresosMes() {
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        List<RegistroAcceso> registrosMes = registroAccesoRepository.findByFechaHoraBetween(inicioMes, finMes);

        double ingresosPorHora = registrosMes.stream()
                .filter(r -> r.getModalidad() == Modalidad.POR_HORA && r.getMontoCalculado() != null)
                .mapToDouble(RegistroAcceso::getMontoCalculado)
                .sum();

        long pensionesActivas = pensionRepository.countByEstado(EstadoPension.ACTIVA);
        Configuracion config = configuracionService.obtenerConfiguracion();
        double ingresosPension = pensionesActivas * config.getTarifaPensionMensual();

        // Agrupar ingresos por día
        Map<String, Double> ingresosPorDia = registrosMes.stream()
                .filter(r -> r.getModalidad() == Modalidad.POR_HORA && r.getMontoCalculado() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getFechaHora().toLocalDate().toString(),
                        java.util.stream.Collectors.summingDouble(RegistroAcceso::getMontoCalculado)
                ));

        int totalAccesosPorHora = (int) registrosMes.stream()
                .filter(r -> r.getModalidad() == Modalidad.POR_HORA)
                .count();

        int totalAccesosPension = (int) registrosMes.stream()
                .filter(r -> r.getModalidad() == Modalidad.PENSION)
                .count();

        return IngresosMesResponse.builder()
                .mes(mesActual.getMonthValue())
                .anio(mesActual.getYear())
                .totalIngresos(ingresosPorHora + ingresosPension)
                .ingresosPorHora(ingresosPorHora)
                .ingresosPension(ingresosPension)
                .totalAccesosPorHora(totalAccesosPorHora)
                .totalAccesosPension(totalAccesosPension)
                .ingresosPorDia(ingresosPorDia)
                .build();
    }

    public AccesosPorModalidadResponse obtenerAccesosPorModalidad(Modalidad modalidad) {
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        List<RegistroAcceso> registrosMes = registroAccesoRepository.findByFechaHoraBetween(inicioMes, finMes)
                .stream()
                .filter(r -> r.getModalidad() == modalidad)
                .toList();

        long totalAccesos = registroAccesoRepository.findAll().stream()
                .filter(r -> r.getModalidad() == modalidad)
                .count();

        long vehiculosActivos = contarVehiculosActualesPorModalidad(modalidad);

        double ingresosMes = 0.0;
        if (modalidad == Modalidad.POR_HORA) {
            ingresosMes = registrosMes.stream()
                    .filter(r -> r.getMontoCalculado() != null)
                    .mapToDouble(RegistroAcceso::getMontoCalculado)
                    .sum();
        } else {
            Configuracion config = configuracionService.obtenerConfiguracion();
            long pensionesActivas = pensionRepository.countByEstado(EstadoPension.ACTIVA);
            ingresosMes = pensionesActivas * config.getTarifaPensionMensual();
        }

        // Calcular promedio de tiempo de estancia (solo para salidas)
        double promedioTiempo = registrosMes.stream()
                .filter(r -> r.getTipo() == EstadoRegistro.SALIDA && r.getMontoCalculado() != null)
                .mapToDouble(r -> {
                    // Buscar la entrada correspondiente
                    return registroAccesoRepository
                            .findTopByVehiculoIdAndTipoOrderByFechaHoraDesc(r.getVehiculoId(), EstadoRegistro.ENTRADA)
                            .map(entrada -> {
                                long minutos = java.time.Duration.between(entrada.getFechaHora(), r.getFechaHora()).toMinutes();
                                return minutos / 60.0; // Convertir a horas
                            })
                            .orElse(0.0);
                })
                .average()
                .orElse(0.0);

        return AccesosPorModalidadResponse.builder()
                .modalidad(modalidad)
                .totalAccesos(totalAccesos)
                .accesosMesActual(registrosMes.size())
                .vehiculosActivos(vehiculosActivos)
                .ingresosMesActual(ingresosMes)
                .promedioTiempoEstancia(promedioTiempo)
                .build();
    }

    private long contarVehiculosActualesPorModalidad(Modalidad modalidad) {
        LocalDateTime haceUnDia = LocalDateTime.now().minusDays(1);
        List<RegistroAcceso> entradas = registroAccesoRepository.findEntradasActivasPorModalidad(modalidad, haceUnDia);

        return entradas.stream()
                .filter(entrada -> {
                    return registroAccesoRepository
                            .findTopByVehiculoIdAndTipoOrderByFechaHoraDesc(entrada.getVehiculoId(), EstadoRegistro.SALIDA)
                            .map(salida -> salida.getFechaHora().isBefore(entrada.getFechaHora()))
                            .orElse(true);
                })
                .count();
    }
}