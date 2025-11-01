package com.coave.coave.controllers;

import com.coave.coave.dtos.ConfiguracionRequest;
import com.coave.coave.dtos.EstadisticasResponse;
import com.coave.coave.dtos.RegistroUsuarioRequest;
import com.coave.coave.models.Configuracion;
import com.coave.coave.models.RegistroAcceso;
import com.coave.coave.models.Usuario;
import com.coave.coave.models.Vehiculo;
import com.coave.coave.models.enums.Rol;
import com.coave.coave.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    private final UsuarioService usuarioService;
    private final VehiculoService vehiculoService;
    private final AccesoService accesoService;
    private final ConfiguracionService configuracionService;
    private final EstadisticasService estadisticasService;
    private final AuthService authService;

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/usuarios/rol/{rol}")
    public ResponseEntity<List<Usuario>> obtenerUsuariosPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(usuarioService.obtenerPorRol(rol));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.ok(authService.registrarUsuario(request));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable String id,
            @RequestBody Usuario usuario
    ) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuarios/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable String id) {
        usuarioService.activar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vehiculos")
    public ResponseEntity<List<Vehiculo>> obtenerVehiculos() {
        return ResponseEntity.ok(vehiculoService.obtenerTodos());
    }

    @GetMapping("/registros")
    public ResponseEntity<List<RegistroAcceso>> obtenerRegistros() {
        return ResponseEntity.ok(accesoService.obtenerTodos());
    }

    @GetMapping("/configuracion")
    public ResponseEntity<Configuracion> obtenerConfiguracion() {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracion());
    }

    @PutMapping("/configuracion")
    public ResponseEntity<Configuracion> actualizarConfiguracion(
            @Valid @RequestBody ConfiguracionRequest request
    ) {
        return ResponseEntity.ok(configuracionService.actualizar(request));
    }
}