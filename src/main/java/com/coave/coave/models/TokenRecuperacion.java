package com.coave.coave.models;

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
@Document(collection = "tokens_recuperacion")
public class TokenRecuperacion {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    private String usuarioId;

    private String email;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion;

    private boolean usado;
}