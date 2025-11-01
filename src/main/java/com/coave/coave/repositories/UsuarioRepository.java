package com.coave.coave.repositories;

import com.coave.coave.models.Usuario;
import com.coave.coave.models.enums.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivo(boolean activo);

    boolean existsByEmail(String email);
}