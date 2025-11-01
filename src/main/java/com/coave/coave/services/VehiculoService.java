package com.coave.coave.services;

import com.coave.coave.dtos.VehiculoRequest;
import com.coave.coave.models.Vehiculo;
import com.coave.coave.repositories.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public Vehiculo registrar(VehiculoRequest request, String conductorId) {
        if (vehiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new RuntimeException("La placa ya está registrada");
        }

        if (vehiculoRepository.existsByConductorIdAndActivo(conductorId, true)) {
            throw new RuntimeException("El conductor ya tiene un vehículo registrado");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(request.getPlaca().toUpperCase())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .conductorId(conductorId)
                .fechaRegistro(LocalDateTime.now())
                .activo(true)
                .build();

        return vehiculoRepository.save(vehiculo);
    }

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public Vehiculo obtenerPorId(String id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
    }

    public List<Vehiculo> obtenerPorConductor(String conductorId) {
        return vehiculoRepository.findByConductorId(conductorId);
    }

    public Vehiculo actualizar(String id, VehiculoRequest request) {
        Vehiculo vehiculo = obtenerPorId(id);

        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());

        return vehiculoRepository.save(vehiculo);
    }

    public void eliminar(String id) {
        Vehiculo vehiculo = obtenerPorId(id);
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }
}