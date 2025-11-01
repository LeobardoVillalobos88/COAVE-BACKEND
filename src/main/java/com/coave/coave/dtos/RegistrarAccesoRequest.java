package com.coave.coave.dtos;

import com.coave.coave.models.enums.EstadoRegistro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarAccesoRequest {

    @NotBlank(message = "El código QR es obligatorio")
    private String codigoQr;

    @NotNull(message = "El tipo de registro es obligatorio")
    private EstadoRegistro tipo;

    private String notas;
}
