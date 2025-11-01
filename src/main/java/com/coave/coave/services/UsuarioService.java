package com.coave.coave.services;

import com.coave.coave.models.Usuario;
import com.coave.coave.models.enums.Rol;
import com.coave.coave.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> obtenerPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Usuario actualizar(String id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerPorId(id);

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setFechaActualizacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public void eliminar(String id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(false);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public void activar(String id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(true);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}