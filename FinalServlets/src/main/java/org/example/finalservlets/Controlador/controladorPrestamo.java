package org.example.finalservlets.Controlador;

import org.example.finalservlets.Modelo.Ejemplar;
import org.example.finalservlets.Modelo.Prestamo;
import org.example.finalservlets.Modelo.Usuario;

import java.time.LocalDate;

public class controladorPrestamo {

    public void validarRegistrarPrestamo(Usuario usuario, Ejemplar ejemplar) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        if (ejemplar == null) {
            throw new IllegalArgumentException("El ejemplar no existe.");
        }
        if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
            throw new IllegalStateException("El usuario tiene una penalización activa.");
        }
        if (!"Disponible".equalsIgnoreCase(ejemplar.getEstado())) {
            throw new IllegalStateException("El ejemplar no está disponible.");
        }
        if (usuario.getPrestamos().stream().count() >= 3) {
            throw new IllegalStateException("El usuario ya tiene 3 préstamos.");
        }
    }

    public void prepararPrestamo(Prestamo prestamo, Usuario usuario, Ejemplar ejemplar) {
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(15));
        ejemplar.setEstado("Prestado");
    }

    public void prepararDevolucion(Prestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("El préstamo no existe.");
        }
        prestamo.setFechaDevolucion(LocalDate.now());
        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado("Disponible");
    }
}
