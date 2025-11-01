package com.coave.coave.util;

import com.coave.coave.models.Usuario;
import com.coave.coave.models.enums.Rol;
import com.coave.coave.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            log.info("Creando usuario administrador por defecto...");

            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@coave.com")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMINISTRADOR)
                    .telefono("1234567890")
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador creado exitosamente");
            log.info("Email: admin@coave.com");
            log.info("Contraseña: admin123");
        }
    }
}