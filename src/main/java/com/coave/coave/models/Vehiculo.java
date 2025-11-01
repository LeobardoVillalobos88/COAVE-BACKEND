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
@Document(collection = "vehiculos")
public class Vehiculo {

    @Id
    private String id;

    @Indexed(unique = true)
    private String placa;

    private String marca;

    private String modelo;

    private String color;

    private String conductorId;

    private LocalDateTime fechaRegistro;

    private boolean activo;
}
