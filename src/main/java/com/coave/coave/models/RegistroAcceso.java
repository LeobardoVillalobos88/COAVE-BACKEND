package com.coave.coave.models;

import com.coave.coave.models.enums.EstadoRegistro;
import com.coave.coave.models.enums.Modalidad;
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
@Document(collection = "registros_acceso")
public class RegistroAcceso {

    @Id
    private String id;

    private String vehiculoId;

    private String conductorId;

    private Modalidad modalidad;

    private EstadoRegistro tipo;

    private LocalDateTime fechaHora;

    private String guardiaId;

    private String codigoQr;

    private Double montoCalculado;

    private String notas;
}
