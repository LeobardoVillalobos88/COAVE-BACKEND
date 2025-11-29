package com.coave.coave.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public void enviarEmailRecuperacion(String destinatario, String codigo, String nombreUsuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("COAVE - Código de Recuperación de Contraseña");

            String contenidoHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2563eb; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { background-color: #f9fafb; padding: 30px; border-radius: 0 0 8px 8px; }
                        .code-container { background-color: #1e3a5f; color: white; padding: 20px; text-align: center; border-radius: 8px; margin: 25px 0; }
                        .code { font-size: 32px; font-weight: bold; letter-spacing: 5px; font-family: 'Courier New', monospace; }
                        .warning { background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 10px 15px; margin: 20px 0; }
                        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>COAVE</h1>
                            <p>Sistema de Control de Acceso Vehicular a Estacionamiento</p>
                        </div>
                        <div class="content">
                            <h2>Hola, %s</h2>
                            <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en COAVE.</p>
                            <p>Tu código de recuperación es:</p>
                            <div class="code-container">
                                <span class="code">%s</span>
                            </div>
                            <p>Ingresa este código en la aplicación para crear una nueva contraseña.</p>
                            <div class="warning">
                                <strong>Importante:</strong> Este código expirará en <strong>15 minutos</strong> por motivos de seguridad.
                            </div>
                            <p>Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña actual seguirá siendo la misma.</p>
                        </div>
                        <div class="footer">
                            <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                            <p>&copy; 2025 COAVE - Todos los derechos reservados</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nombreUsuario, codigo);

            helper.setText(contenidoHtml, true);
            mailSender.send(message);

            log.info("Email de recuperación enviado exitosamente a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar email de recuperación a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación", e);
        }
    }

    public void enviarEmailConfirmacionCambio(String destinatario, String nombreUsuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("COAVE - Contraseña Actualizada");

            String contenidoHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #059669; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { background-color: #f9fafb; padding: 30px; border-radius: 0 0 8px 8px; }
                        .success { background-color: #d1fae5; border-left: 4px solid #059669; padding: 10px 15px; margin: 20px 0; }
                        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>COAVE</h1>
                            <p>Sistema de Control de Acceso Vehicular a Estacionamiento</p>
                        </div>
                        <div class="content">
                            <h2>Hola, %s</h2>
                            <div class="success">
                                <strong>Tu contraseña ha sido actualizada exitosamente.</strong>
                            </div>
                            <p>Si no realizaste este cambio, por favor contacta a soporte inmediatamente.</p>
                        </div>
                        <div class="footer">
                            <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                            <p>&copy; 2025 COAVE - Todos los derechos reservados</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nombreUsuario);

            helper.setText(contenidoHtml, true);
            mailSender.send(message);

            log.info("Email de confirmación de cambio enviado a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar email de confirmación a {}: {}", destinatario, e.getMessage());
        }
    }
}