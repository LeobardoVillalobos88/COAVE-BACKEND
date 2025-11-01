package com.coave.coave.models;

import com.coave.coave.models.enums.EstadoPension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pensiones")
public class Pension {

    @Id
    private String id;

    private String conductorId;

    private String vehiculoId;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Double monto;

    private EstadoPension estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaRenovacion;

    private String notas;
}