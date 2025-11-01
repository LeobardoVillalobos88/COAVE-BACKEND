package com.coave.coave.repositories;

import com.coave.coave.models.Pension;
import com.coave.coave.models.enums.EstadoPension;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PensionRepository extends MongoRepository<Pension, String> {

    List<Pension> findByConductorId(String conductorId);

    Optional<Pension> findByConductorIdAndEstado(String conductorId, EstadoPension estado);

    Optional<Pension> findByVehiculoIdAndEstado(String vehiculoId, EstadoPension estado);

    List<Pension> findByEstado(EstadoPension estado);

    List<Pension> findByFechaFinBefore(LocalDate fecha);

    long countByEstado(EstadoPension estado);
}