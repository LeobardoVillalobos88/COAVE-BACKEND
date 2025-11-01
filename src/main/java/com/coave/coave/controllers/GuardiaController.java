package com.coave.coave.controllers;

import com.coave.coave.dtos.RegistrarAccesoRequest;
import com.coave.coave.dtos.RegistrarAccesoResponse;
import com.coave.coave.dtos.RegistrarEntradaSalidaRequest;
import com.coave.coave.models.RegistroAcceso;
import com.coave.coave.models.enums.EstadoRegistro;
import com.coave.coave.services.AccesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guardia")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GUARDIA', 'ADMINISTRADOR')")
public class GuardiaController {

    private final AccesoService accesoService;

    @PostMapping("/accesos/entrada")
    public ResponseEntity<RegistrarAccesoResponse> registrarEntrada(
            @Valid @RequestBody RegistrarEntradaSalidaRequest request
    ) {
        RegistrarAccesoRequest accesoRequest = RegistrarAccesoRequest.builder()
                .codigoQr(request.getCodigoQr())
                .tipo(EstadoRegistro.ENTRADA)
                .notas(request.getNotas())
                .build();
        return ResponseEntity.ok(accesoService.registrarAcceso(accesoRequest));
    }

    @PostMapping("/accesos/salida")
    public ResponseEntity<RegistrarAccesoResponse> registrarSalida(
            @Valid @RequestBody RegistrarEntradaSalidaRequest request
    ) {
        RegistrarAccesoRequest accesoRequest = RegistrarAccesoRequest.builder()
                .codigoQr(request.getCodigoQr())
                .tipo(EstadoRegistro.SALIDA)
                .notas(request.getNotas())
                .build();
        return ResponseEntity.ok(accesoService.registrarAcceso(accesoRequest));
    }

    @GetMapping("/accesos/activos")
    public ResponseEntity<List<RegistroAcceso>> obtenerAccesosActivos() {
        return ResponseEntity.ok(accesoService.obtenerAccesosActivos());
    }

    @GetMapping("/accesos/historial")
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorial() {
        return ResponseEntity.ok(accesoService.obtenerTodos());
    }

    @PostMapping("/registrar-acceso")
    public ResponseEntity<RegistrarAccesoResponse> registrarAcceso(
            @Valid @RequestBody RegistrarAccesoRequest request
    ) {
        return ResponseEntity.ok(accesoService.registrarAcceso(request));
    }

    @GetMapping("/registros")
    public ResponseEntity<List<RegistroAcceso>> obtenerRegistros() {
        return ResponseEntity.ok(accesoService.obtenerTodos());
    }
}