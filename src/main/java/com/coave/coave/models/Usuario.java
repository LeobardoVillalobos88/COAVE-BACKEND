package com.coave.coave.models;

import com.coave.coave.models.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String email;

    private String contrasena;

    private Rol rol;

    private String telefono;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    private boolean activo;

    // <CHANGE> Nuevos campos para OAuth2
    private String provider;        // "local" o "google"
    private String providerId;      // ID del usuario en Google
    private String avatarUrl;       // Foto de perfil de Google
}