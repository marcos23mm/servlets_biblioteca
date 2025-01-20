package org.example.finalservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.finalservlets.Modelo.DAOGenerico;
import org.example.finalservlets.Modelo.Ejemplar;
import org.example.finalservlets.Modelo.Prestamo;
import org.example.finalservlets.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;


@WebServlet(name = "PrestamoServlet", value = "/PrestamoServlet")
public class PrestamoServlet extends HttpServlet {

    private DAOGenerico<Prestamo, Integer> daoPrestamo;
    private DAOGenerico<Usuario, Integer> daoUsuario;
    private DAOGenerico<Ejemplar, Integer> daoEjemplar;
    private controladorPrestamo controladorPrestamo;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Inicializando PrestamoServlet...");
            daoPrestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
            daoUsuario = new DAOGenerico<>(Usuario.class, Integer.class);
            daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
            controladorPrestamo = new controladorPrestamo();
            System.out.println("PrestamoServlet inicializado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar DAOGenerico: " + e.getMessage());
            throw new ServletException("Error al inicializar DAOGenerico", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        String action = request.getParameter("action");
        String json_response = "";

        try {
            switch (action) {
                case "add": // Registrar un préstamo
                    Integer usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
                    Integer ejemplarId = Integer.parseInt(request.getParameter("ejemplarId"));

                    Usuario usuario = daoUsuario.getById(usuarioId);
                    Ejemplar ejemplar = daoEjemplar.getById(ejemplarId);

                    try {
                        // Validaciones
                        controladorPrestamo.validarRegistrarPrestamo(usuario, ejemplar);

                        // Preparar el préstamo
                        Prestamo prestamo = new Prestamo();
                        controladorPrestamo.prepararPrestamo(prestamo, usuario, ejemplar);

                        // Persistencia
                        boolean prestamoAgregado = daoPrestamo.add(prestamo);

                        // Actualizar el estado del ejemplar
                        daoEjemplar.update(ejemplar); 

                        json_response = conversorJson.writeValueAsString(
                                prestamoAgregado ? "Préstamo registrado correctamente." : "Error al registrar el préstamo."
                        );
                    } catch (Exception e) {
                        json_response = conversorJson.writeValueAsString("Error: " + e.getMessage());
                    }
                    break;


                case "return": 
                    Integer prestamoId = Integer.parseInt(request.getParameter("prestamoId"));

                    Prestamo prestamo = daoPrestamo.getById(prestamoId);

                    try {
                        
                        controladorPrestamo.prepararDevolucion(prestamo);

                        
                        Prestamo updatedPrestamo = daoPrestamo.update(prestamo);

                        json_response = conversorJson.writeValueAsString(
                                updatedPrestamo != null ? "Devolución registrada correctamente." : "Error al registrar la devolución."
                        );
                    } catch (Exception e) {
                        json_response = conversorJson.writeValueAsString("Error: " + e.getMessage());
                    }
                    break;

                default:
                    json_response = conversorJson.writeValueAsString("Acción no válida.");
                    break;
            }
        } catch (Exception e) {
            json_response = conversorJson.writeValueAsString("Error al procesar la solicitud: " + e.getMessage());
        }

        impresora.println(json_response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        try {
            List<Prestamo> listaPrestamos = daoPrestamo.getAll();
            String json_response = conversorJson.writeValueAsString(listaPrestamos);
            impresora.println(json_response);
        } catch (Exception e) {
            String errorJson = conversorJson.writeValueAsString("Error al obtener los préstamos: " + e.getMessage());
            impresora.println(errorJson);
        }
    }

    @Override
    public void destroy() {
        try {
            if (daoPrestamo != null) daoPrestamo.close();
            if (daoUsuario != null) daoUsuario.close();
            if (daoEjemplar != null) daoEjemplar.close();
        } catch (Exception e) {
            System.err.println("Error al liberar recursos de DAO: " + e.getMessage());
        }
    }
}

