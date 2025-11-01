package com.coave.coave.services;

import com.coave.coave.dtos.GenerarQRRequest;
import com.coave.coave.dtos.GenerarQRResponse;
import com.coave.coave.models.CodigoQR;
import com.coave.coave.models.Pension;
import com.coave.coave.models.Vehiculo;
import com.coave.coave.models.enums.EstadoPension;
import com.coave.coave.models.enums.Modalidad;
import com.coave.coave.repositories.CodigoQRRepository;
import com.coave.coave.repositories.PensionRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRService {

    private final CodigoQRRepository codigoQRRepository;
    private final VehiculoService vehiculoService;
    private final PensionRepository pensionRepository;

    @Value("${qr.expiracion-minutos}")
    private Integer expiracionMinutos;

    public GenerarQRResponse generarQR(GenerarQRRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Vehiculo vehiculo = vehiculoService.obtenerPorId(request.getVehiculoId());

        if (request.getModalidad() == Modalidad.PENSION) {
            Pension pension = pensionRepository.findByVehiculoIdAndEstado(vehiculo.getId(), EstadoPension.ACTIVA)
                    .orElseThrow(() -> new RuntimeException("No tiene una pensión activa"));

            if (pension.getFechaFin().isBefore(java.time.LocalDate.now())) {
                throw new RuntimeException("La pensión ha vencido");
            }
        }

        String codigo = UUID.randomUUID().toString();
        LocalDateTime fechaGeneracion = LocalDateTime.now();
        LocalDateTime fechaExpiracion = fechaGeneracion.plusMinutes(expiracionMinutos);

        CodigoQR codigoQR = CodigoQR.builder()
                .codigo(codigo)
                .conductorId(vehiculo.getConductorId())
                .vehiculoId(vehiculo.getId())
                .modalidad(request.getModalidad())
                .fechaGeneracion(fechaGeneracion)
                .fechaExpiracion(fechaExpiracion)
                .usado(false)
                .build();

        codigoQR = codigoQRRepository.save(codigoQR);

        String imagenBase64 = generarImagenQR(codigo);

        return GenerarQRResponse.builder()
                .codigoQrId(codigoQR.getId())
                .codigo(codigo)
                .imagenBase64(imagenBase64)
                .modalidad(request.getModalidad())
                .fechaGeneracion(fechaGeneracion)
                .fechaExpiracion(fechaExpiracion)
                .build();
    }

    private String generarImagenQR(String codigo) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(codigo, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar imagen QR: " + e.getMessage());
        }
    }

    public CodigoQR validarQR(String codigo, boolean permitirUsado) {
        if (permitirUsado) {
            // Para salida: permitir QR ya usado, solo validar que no esté expirado
            return codigoQRRepository.findByCodigoAndFechaExpiracionAfter(codigo, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Código QR inválido o expirado"));
        } else {
            // Para entrada: validar que no esté usado y no esté expirado
            return codigoQRRepository.findByCodigoAndUsadoFalseAndFechaExpiracionAfter(codigo, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Código QR inválido o expirado"));
        }
    }

    public CodigoQR validarQR(String codigo) {
        return validarQR(codigo, false);
    }

    public void marcarComoUsado(String codigoQRId, String registroAccesoId) {
        CodigoQR codigoQR = codigoQRRepository.findById(codigoQRId)
                .orElseThrow(() -> new RuntimeException("Código QR no encontrado"));

        codigoQR.setUsado(true);
        codigoQR.setRegistroAccesoId(registroAccesoId);
        codigoQRRepository.save(codigoQR);
    }

    public List<CodigoQR> obtenerPorConductor(String conductorId) {
        return codigoQRRepository.findByConductorId(conductorId);
    }

    public byte[] obtenerImagenQR(String codigo) {
        CodigoQR codigoQR = codigoQRRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Código QR no encontrado"));

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(codigo, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar imagen QR: " + e.getMessage());
        }
    }
}