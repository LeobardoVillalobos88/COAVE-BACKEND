package com.coave.coave.repositories;

import com.coave.coave.models.TokenRecuperacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends MongoRepository<TokenRecuperacion, String> {

    Optional<TokenRecuperacion> findByTokenAndUsadoFalse(String token);

    Optional<TokenRecuperacion> findByToken(String token);

    List<TokenRecuperacion> findByUsuarioIdAndUsadoFalse(String usuarioId);

    void deleteByFechaExpiracionBefore(LocalDateTime fecha);

    void deleteByUsuarioId(String usuarioId);
}