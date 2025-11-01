package com.coave.coave.controllers;

import com.coave.coave.dtos.*;
import com.coave.coave.models.*;
import com.coave.coave.repositories.CodigoQRRepository;
import com.coave.coave.repositories.UsuarioRepository;
import com.coave.coave.services.AccesoService;
import com.coave.coave.services.PensionService;
import com.coave.coave.services.QRService;
import com.coave.coave.services.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/conductor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CONDUCTOR', 'ADMINISTRADOR')")
public class ConductorController {

    private final VehiculoService vehiculoService;
    private final QRService qrService;
    private final AccesoService accesoService;
    private final PensionService pensionService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/vehiculos")
    public ResponseEntity<Vehiculo> registrarVehiculo(@Valid @RequestBody VehiculoRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return ResponseEntity.ok(vehiculoService.registrar(request, conductor.getId()));
    }

    @GetMapping("/vehiculos")
    public ResponseEntity<List<Vehiculo>> obtenerMisVehiculos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return ResponseEntity.ok(vehiculoService.obtenerPorConductor(conductor.getId()));
    }

    @PutMapping("/vehiculos/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(
            @PathVariable String id,
            @Valid @RequestBody VehiculoRequest request
    ) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, request));
    }

    @DeleteMapping("/vehiculos/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable String id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generar-qr")
    public ResponseEntity<GenerarQRResponse> generarQR(@Valid @RequestBody GenerarQRRequest request) {
        return ResponseEntity.ok(qrService.generarQR(request));
    }

    @GetMapping("/mis-codigos")
    public ResponseEntity<List<CodigoQR>> obtenerMisCodigosQR() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return ResponseEntity.ok(qrService.obtenerPorConductor(conductor.getId()));
    }

    @GetMapping("/qr/{codigo}/imagen")
    public ResponseEntity<byte[]> obtenerImagenQR(@PathVariable String codigo) {
        byte[] imagenBytes = qrService.obtenerImagenQR(codigo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(imagenBytes);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorial() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return ResponseEntity.ok(accesoService.obtenerHistorial(conductor.getId()));
    }

    @PostMapping("/pensiones")
    public ResponseEntity<Pension> crearPension(@Valid @RequestBody ContratarPensionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        PensionRequest pensionRequest = PensionRequest.builder()
                .conductorId(conductor.getId())
                .vehiculoId(request.getVehiculoId())
                .fechaInicio(LocalDate.now())
                .notas(request.getNotas())
                .build();

        return ResponseEntity.ok(pensionService.crear(pensionRequest));
    }

    @PostMapping("/pensiones/contratar")
    public ResponseEntity<Pension> contratarPension(@Valid @RequestBody ContratarPensionRequest request) {
        return crearPension(request);
    }

    @GetMapping("/pensiones/mi-pension")
    public ResponseEntity<List<Pension>> obtenerMisPensiones() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario conductor = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        return ResponseEntity.ok(pensionService.obtenerPorConductor(conductor.getId()));
    }

    @PutMapping("/pensiones/{id}/renovar")
    public ResponseEntity<Pension> renovarPension(@PathVariable String id) {
        return ResponseEntity.ok(pensionService.renovar(id));
    }

    @DeleteMapping("/pensiones/{id}")
    public ResponseEntity<Void> cancelarPension(@PathVariable String id) {
        pensionService.cancelar(id);
        return ResponseEntity.ok().build();
    }
}