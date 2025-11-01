package com.coave.coave.repositories;

import com.coave.coave.models.RegistroAcceso;
import com.coave.coave.models.enums.EstadoRegistro;
import com.coave.coave.models.enums.Modalidad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAccesoRepository extends MongoRepository<RegistroAcceso, String> {

    List<RegistroAcceso> findByVehiculoId(String vehiculoId);

    List<RegistroAcceso> findByConductorId(String conductorId);

    List<RegistroAcceso> findByModalidad(Modalidad modalidad);

    List<RegistroAcceso> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<RegistroAcceso> findTopByVehiculoIdAndTipoOrderByFechaHoraDesc(String vehiculoId, EstadoRegistro tipo);

    @Query("{ 'vehiculoId': ?0, 'tipo': 'ENTRADA', 'fechaHora': { $gte: ?1 } }")
    Optional<RegistroAcceso> findUltimaEntradaSinSalida(String vehiculoId, LocalDateTime desde);

    long countByModalidadAndTipoAndFechaHoraBetween(Modalidad modalidad, EstadoRegistro tipo, LocalDateTime inicio, LocalDateTime fin);

    @Query("{ 'modalidad': ?0, 'tipo': 'ENTRADA', 'fechaHora': { $gte: ?1 } }")
    List<RegistroAcceso> findEntradasActivasPorModalidad(Modalidad modalidad, LocalDateTime desde);
}