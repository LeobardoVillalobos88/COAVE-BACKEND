package com.coave.coave.controllers;

import com.coave.coave.dtos.LoginRequest;
import com.coave.coave.dtos.LoginResponse;
import com.coave.coave.dtos.RegistroUsuarioRequest;
import com.coave.coave.models.Usuario;
import com.coave.coave.services.AuthService;
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
        // Actualiza la ruta aquí también por consistencia
        String authUrl = "/api/auth/oauth2/authorize/google";
        return ResponseEntity.ok(Map.of("url", authUrl));
    }
}