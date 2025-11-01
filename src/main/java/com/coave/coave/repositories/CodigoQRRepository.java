package com.coave.coave.repositories;

import com.coave.coave.models.CodigoQR;
import com.coave.coave.models.enums.Modalidad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodigoQRRepository extends MongoRepository<CodigoQR, String> {

    Optional<CodigoQR> findByCodigo(String codigo);

    List<CodigoQR> findByConductorId(String conductorId);

    List<CodigoQR> findByVehiculoId(String vehiculoId);

    List<CodigoQR> findByModalidad(Modalidad modalidad);

    List<CodigoQR> findByUsado(boolean usado);

    List<CodigoQR> findByFechaExpiracionBefore(LocalDateTime fecha);

    Optional<CodigoQR> findByCodigoAndUsadoFalseAndFechaExpiracionAfter(String codigo, LocalDateTime fecha);

    Optional<CodigoQR> findByCodigoAndFechaExpiracionAfter(String codigo, LocalDateTime fechaExpiracion);
}