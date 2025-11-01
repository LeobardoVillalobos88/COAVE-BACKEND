package com.coave.coave.repositories;

import com.coave.coave.models.Vehiculo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByConductorId(String conductorId);

    Optional<Vehiculo> findByConductorIdAndActivo(String conductorId, boolean activo);

    boolean existsByPlaca(String placa);

    boolean existsByConductorIdAndActivo(String conductorId, boolean activo);
}