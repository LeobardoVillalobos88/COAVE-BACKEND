package com.coave.coave.dtos;

import com.coave.coave.models.enums.EstadoRegistro;
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
public class RegistrarAccesoResponse {

    private String registroId;

    private String vehiculoPlaca;

    private String conductorNombre;

    private Modalidad modalidad;

    private EstadoRegistro tipo;

    private LocalDateTime fechaHora;

    private Double montoCalculado;

    private String mensaje;
}