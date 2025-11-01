package com.coave.coave.dtos;

import com.coave.coave.models.enums.Modalidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerarQRResponse {

    private String codigoQrId;

    private String codigo;

    private String imagenBase64;

    private Modalidad modalidad;

    private LocalDateTime fechaGeneracion;

    private LocalDateTime fechaExpiracion;
}
