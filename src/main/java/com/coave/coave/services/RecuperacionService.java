package com.coave.coave.services;

import com.coave.coave.dtos.*;
import com.coave.coave.exception.BadRequestException;
import com.coave.coave.exception.ResourceNotFoundException;
import com.coave.coave.models.TokenRecuperacion;
import com.coave.coave.models.Usuario;
import com.coave.coave.repositories.TokenRecuperacionRepository;
import com.coave.coave.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecuperacionService {

    private final TokenRecuperacionRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${recuperacion.token.expiracion-minutos:15}")
    private int tokenExpiracionMinutos;

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 8;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public MensajeResponse solicitarRecuperacion(SolicitarRecuperacionRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            log.warn("Solicitud de recuperación para email no registrado: {}", email);
            return MensajeResponse.exito(
                    "Si el correo está registrado, recibirás un código para restablecer tu contraseña"
            );
        }

        tokenRepository.deleteByUsuarioId(usuario.getId());

        String codigo = generarCodigoRecuperacion();

        TokenRecuperacion tokenRecuperacion = TokenRecuperacion.builder()
                .token(codigo)
                .usuarioId(usuario.getId())
                .email(email)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(tokenExpiracionMinutos))
                .usado(false)
                .build();

        tokenRepository.save(tokenRecuperacion);

        emailService.enviarEmailRecuperacion(email, codigo, usuario.getNombre());

        log.info("Código de recuperación generado para usuario: {}", email);

        return MensajeResponse.exito(
                "Si el correo está registrado, recibirás un código para restablecer tu contraseña"
        );
    }

    public ValidarTokenResponse validarToken(String token) {
        TokenRecuperacion tokenRecuperacion = tokenRepository.findByTokenAndUsadoFalse(token.toUpperCase())
                .orElse(null);

        if (tokenRecuperacion == null) {
            return ValidarTokenResponse.builder()
                    .valido(false)
                    .mensaje("El código no es válido o ya fue utilizado")
                    .build();
        }

        if (tokenRecuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ValidarTokenResponse.builder()
                    .valido(false)
                    .mensaje("El código ha expirado. Solicita uno nuevo")
                    .build();
        }

        long minutosRestantes = ChronoUnit.MINUTES.between(
                LocalDateTime.now(),
                tokenRecuperacion.getFechaExpiracion()
        );

        String emailOculto = ocultarEmail(tokenRecuperacion.getEmail());

        return ValidarTokenResponse.builder()
                .valido(true)
                .email(emailOculto)
                .mensaje("Código válido")
                .minutosRestantes(minutosRestantes)
                .build();
    }

    @Transactional
    public MensajeResponse restablecerContrasena(RestablecerContrasenaRequest request) {
        if (!request.getNuevaContrasena().equals(request.getConfirmarContrasena())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        TokenRecuperacion tokenRecuperacion = tokenRepository.findByTokenAndUsadoFalse(request.getToken().toUpperCase())
                .orElseThrow(() -> new BadRequestException("El código no es válido o ya fue utilizado"));

        if (tokenRecuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código ha expirado. Solicita uno nuevo");
        }

        Usuario usuario = usuarioRepository.findById(tokenRecuperacion.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        tokenRecuperacion.setUsado(true);
        tokenRepository.save(tokenRecuperacion);

        emailService.enviarEmailConfirmacionCambio(usuario.getEmail(), usuario.getNombre());

        log.info("Contraseña restablecida exitosamente para usuario: {}", usuario.getEmail());

        return MensajeResponse.exito("Tu contraseña ha sido actualizada exitosamente");
    }

    @Transactional
    public MensajeResponse cambiarContrasena(String usuarioId, CambiarContrasenaRequest request) {
        if (!request.getNuevaContrasena().equals(request.getConfirmarContrasena())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(request.getNuevaContrasena(), usuario.getContrasena())) {
            throw new BadRequestException("La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        emailService.enviarEmailConfirmacionCambio(usuario.getEmail(), usuario.getNombre());

        log.info("Contraseña cambiada exitosamente para usuario: {}", usuario.getEmail());

        return MensajeResponse.exito("Tu contraseña ha sido actualizada exitosamente");
    }

    public void limpiarTokensExpirados() {
        tokenRepository.deleteByFechaExpiracionBefore(LocalDateTime.now());
        log.info("Tokens de recuperación expirados eliminados");
    }

    private String generarCodigoRecuperacion() {
        StringBuilder codigo = new StringBuilder(LONGITUD_CODIGO);
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            codigo.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return codigo.toString();
    }

    private String ocultarEmail(String email) {
        int indexArroba = email.indexOf("@");
        if (indexArroba <= 2) {
            return "*".repeat(indexArroba) + email.substring(indexArroba);
        }
        return email.substring(0, 2) + "*".repeat(indexArroba - 2) + email.substring(indexArroba);
    }
}