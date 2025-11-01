package com.coave.coave.dtos;

import com.coave.coave.models.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String tipo;

    private String id;

    private String nombre;

    private String email;

    private Rol rol;
}
