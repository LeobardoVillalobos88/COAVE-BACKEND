package com.coave.coave.util;

import com.coave.coave.services.PensionService;
import com.coave.coave.services.RecuperacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final PensionService pensionService;
    private final RecuperacionService recuperacionService;

    @Scheduled(cron = "0 0 0 * * *")
    public void actualizarPensionesVencidas() {
        log.info("Ejecutando tarea programada: actualizar pensiones vencidas");
        pensionService.actualizarPensionesVencidas();
        log.info("Tarea completada: pensiones vencidas actualizadas");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void limpiarTokensExpirados() {
        log.info("Ejecutando tarea programada: limpiar tokens de recuperación expirados");
        recuperacionService.limpiarTokensExpirados();
        log.info("Tarea completada: tokens expirados eliminados");
    }
}