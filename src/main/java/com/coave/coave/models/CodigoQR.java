package com.coave.coave.models;

import com.coave.coave.models.enums.Modalidad;
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
@Document(collection = "codigos_qr")
public class CodigoQR {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String conductorId;

    private String vehiculoId;

    private Modalidad modalidad;

    private LocalDateTime fechaGeneracion;

    private LocalDateTime fechaExpiracion;

    private boolean usado;

    private String registroAccesoId;
}