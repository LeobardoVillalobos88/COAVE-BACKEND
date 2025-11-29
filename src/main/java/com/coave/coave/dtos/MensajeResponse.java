package com.coave.coave.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {

    private String mensaje;
    private boolean exito;
    private LocalDateTime timestamp;

    public static MensajeResponse exito(String mensaje) {
        return MensajeResponse.builder()
                .mensaje(mensaje)
                .exito(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static MensajeResponse error(String mensaje) {
        return MensajeResponse.builder()
                .mensaje(mensaje)
                .exito(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}