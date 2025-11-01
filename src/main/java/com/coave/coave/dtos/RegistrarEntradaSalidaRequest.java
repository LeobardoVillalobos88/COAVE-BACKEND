package com.coave.coave.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEntradaSalidaRequest {

    @NotBlank(message = "El código QR es obligatorio")
    private String codigoQr;

    private String notas;
}