package com.coave.coave.controllers;

import com.coave.coave.dtos.*;
import com.coave.coave.models.Usuario;
import com.coave.coave.services.AuthService;
import com.coave.coave.services.RecuperacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RecuperacionService recuperacionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.ok(authService.registrarUsuario(request));
    }

    @GetMapping("/oauth2/url/google")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        String authUrl = "/api/auth/oauth2/authorize/google";
        return ResponseEntity.ok(Map.of("url", authUrl));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<MensajeResponse> solicitarRecuperacion(
            @Valid @RequestBody SolicitarRecuperacionRequest request) {
        return ResponseEntity.ok(recuperacionService.solicitarRecuperacion(request));
    }

    @GetMapping("/validar-token/{token}")
    public ResponseEntity<ValidarTokenResponse> validarToken(@PathVariable String token) {
        return ResponseEntity.ok(recuperacionService.validarToken(token));
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<MensajeResponse> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaRequest request) {
        return ResponseEntity.ok(recuperacionService.restablecerContrasena(request));
    }
}