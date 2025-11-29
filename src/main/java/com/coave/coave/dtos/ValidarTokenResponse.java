package com.coave.coave.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidarTokenResponse {

    private boolean valido;
    private String email;
    private String mensaje;
    private long minutosRestantes;
}