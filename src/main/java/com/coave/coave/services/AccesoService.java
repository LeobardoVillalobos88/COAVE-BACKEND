package com.coave.coave.services;

import com.coave.coave.dtos.RegistrarAccesoRequest;
import com.coave.coave.dtos.RegistrarAccesoResponse;
import com.coave.coave.models.CodigoQR;
import com.coave.coave.models.Configuracion;
import com.coave.coave.models.RegistroAcceso;
import com.coave.coave.models.Usuario;
import com.coave.coave.models.Vehiculo;
import com.coave.coave.models.enums.EstadoRegistro;
import com.coave.coave.models.enums.Modalidad;
import com.coave.coave.repositories.RegistroAccesoRepository;
import com.coave.coave.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final RegistroAccesoRepository registroAccesoRepository;
    private final QRService qrService;
    private final VehiculoService vehiculoService;
    private final ConfiguracionService configuracionService;
    private final UsuarioRepository usuarioRepository;

    public RegistrarAccesoResponse registrarAcceso(RegistrarAccesoRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario guardia = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guardia no encontrado"));

        boolean permitirUsado = (request.getTipo() == EstadoRegistro.SALIDA);
        CodigoQR codigoQR = qrService.validarQR(request.getCodigoQr(), permitirUsado);
        Vehiculo vehiculo = vehiculoService.obtenerPorId(codigoQR.getVehiculoId());

        if (request.getTipo() == EstadoRegistro.ENTRADA) {
            return registrarEntrada(codigoQR, vehiculo, guardia.getId(), request.getNotas());
        } else {
            return registrarSalida(codigoQR, vehiculo, guardia.getId(), request.getNotas());
        }
    }

    private RegistrarAccesoResponse registrarEntrada(CodigoQR codigoQR, Vehiculo vehiculo, String guardiaId, String notas) {
        Configuracion config = configuracionService.obtenerConfiguracion();

        long vehiculosActuales = contarVehiculosActualesPorModalidad(codigoQR.getModalidad());
        long capacidadDisponible = codigoQR.getModalidad() == Modalidad.POR_HORA
                ? config.getCapacidadPorHora()
                : config.getCapacidadPension();

        if (vehiculosActuales >= capacidadDisponible) {
            throw new RuntimeException("No hay espacios disponibles para la modalidad " + codigoQR.getModalidad());
        }

        RegistroAcceso registro = RegistroAcceso.builder()
                .vehiculoId(vehiculo.getId())
                .conductorId(codigoQR.getConductorId())
                .modalidad(codigoQR.getModalidad())
                .tipo(EstadoRegistro.ENTRADA)
                .fechaHora(LocalDateTime.now())
                .guardiaId(guardiaId)
                .codigoQr(codigoQR.getCodigo())
                .notas(notas)
                .build();

        registro = registroAccesoRepository.save(registro);
        qrService.marcarComoUsado(codigoQR.getId(), registro.getId());

        return RegistrarAccesoResponse.builder()
                .registroId(registro.getId())
                .vehiculoPlaca(vehiculo.getPlaca())
                .conductorNombre("Conductor")
                .modalidad(codigoQR.getModalidad())
                .tipo(EstadoRegistro.ENTRADA)
                .fechaHora(registro.getFechaHora())
                .mensaje("Entrada registrada exitosamente")
                .build();
    }

    private RegistrarAccesoResponse registrarSalida(CodigoQR codigoQR, Vehiculo vehiculo, String guardiaId, String notas) {
        RegistroAcceso ultimaEntrada = registroAccesoRepository
                .findTopByVehiculoIdAndTipoOrderByFechaHoraDesc(vehiculo.getId(), EstadoRegistro.ENTRADA)
                .orElseThrow(() -> new RuntimeException("No se encontró registro de entrada"));

        Double montoCalculado = 0.0;

        if (codigoQR.getModalidad() == Modalidad.POR_HORA) {
            Configuracion config = configuracionService.obtenerConfiguracion();
            long minutos = Duration.between(ultimaEntrada.getFechaHora(), LocalDateTime.now()).toMinutes();
            long horas = (long) Math.ceil(minutos / 60.0);
            montoCalculado = horas * config.getTarifaPorHora();
        }

        RegistroAcceso registro = RegistroAcceso.builder()
                .vehiculoId(vehiculo.getId())
                .conductorId(codigoQR.getConductorId())
                .modalidad(codigoQR.getModalidad())
                .tipo(EstadoRegistro.SALIDA)
                .fechaHora(LocalDateTime.now())
                .guardiaId(guardiaId)
                .codigoQr(codigoQR.getCodigo())
                .montoCalculado(montoCalculado)
                .notas(notas)
                .build();

        registro = registroAccesoRepository.save(registro);

        return RegistrarAccesoResponse.builder()
                .registroId(registro.getId())
                .vehiculoPlaca(vehiculo.getPlaca())
                .conductorNombre("Conductor")
                .modalidad(codigoQR.getModalidad())
                .tipo(EstadoRegistro.SALIDA)
                .fechaHora(registro.getFechaHora())
                .montoCalculado(montoCalculado)
                .mensaje(codigoQR.getModalidad() == Modalidad.POR_HORA
                        ? "Salida registrada. Monto a pagar: $" + montoCalculado
                        : "Salida registrada exitosamente")
                .build();
    }

    public List<RegistroAcceso> obtenerHistorial(String conductorId) {
        List<RegistroAcceso> registros = registroAccesoRepository.findByConductorId(conductorId);
        registros.sort(Comparator.comparing(RegistroAcceso::getFechaHora).reversed());
        return registros;
    }

    public List<RegistroAcceso> obtenerTodos() {
        return registroAccesoRepository.findAll();
    }

    public Page<RegistroAcceso> obtenerTodosPaginado(Pageable pageable) {
        return registroAccesoRepository.findAll(pageable);
    }

    public List<RegistroAcceso> obtenerAccesosActivos() {
        LocalDateTime haceUnDia = LocalDateTime.now().minusDays(1);
        List<RegistroAcceso> todasEntradas = registroAccesoRepository
                .findEntradasActivasPorModalidad(Modalidad.POR_HORA, haceUnDia);

        List<RegistroAcceso> entradasPension = registroAccesoRepository
                .findEntradasActivasPorModalidad(Modalidad.PENSION, haceUnDia);

        todasEntradas.addAll(entradasPension);

        return todasEntradas.stream()
                .filter(entrada -> {
                    return registroAccesoRepository
                            .findTopByVehiculoIdAndTipoOrderByFechaHoraDesc(entrada.getVehiculoId(), EstadoRegistro.SALIDA)
                            .map(salida -> salida.getFechaHora().isBefore(entrada.getFechaHora()))
                            .orElse(true);
                })
                .sorted(Comparator.comparing(RegistroAcceso::getFechaHora).reversed())
                .collect(Collectors.toList());
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