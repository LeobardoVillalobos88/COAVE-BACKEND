package com.coave.coave.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "configuracion")
public class Configuracion {

    @Id
    private String id;

    private Integer capacidadTotal;

    private Integer capacidadPorHora;

    private Integer capacidadPension;

    private Double tarifaPorHora;

    private Double tarifaPensionMensual;

    private Integer qrExpiracionMinutos;

    private LocalDateTime fechaActualizacion;
}