package com.coave.coave.controllers;

import com.coave.coave.dtos.CambiarContrasenaRequest;
import com.coave.coave.dtos.MensajeResponse;
import com.coave.coave.services.RecuperacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final RecuperacionService recuperacionService;

    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeResponse> cambiarContrasena(
            Authentication authentication,
            @Valid @RequestBody CambiarContrasenaRequest request) {
        String usuarioId = authentication.getName();
        return ResponseEntity.ok(recuperacionService.cambiarContrasena(usuarioId, request));
    }
}