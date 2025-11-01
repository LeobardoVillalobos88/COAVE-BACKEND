package com.coave.coave.repositories;

import com.coave.coave.models.Configuracion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends MongoRepository<Configuracion, String> {

    Optional<Configuracion> findFirstByOrderByFechaActualizacionDesc();
}