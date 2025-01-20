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

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Inicializando PrestamoServlet...");
            daoPrestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
            daoUsuario = new DAOGenerico<>(Usuario.class, Integer.class);
            daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
            System.out.println("DAOGenerico para Prestamo, Usuario y Ejemplar inicializado correctamente.");
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
                case "add":
                    Integer usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
                    Integer ejemplarId = Integer.parseInt(request.getParameter("ejemplarId"));
                    LocalDate fechaInicio = LocalDate.parse(request.getParameter("fechaInicio"));

                    Usuario usuario = daoUsuario.getById(usuarioId);
                    Ejemplar ejemplar = daoEjemplar.getById(ejemplarId);

                    if (usuario == null) {
                        json_response = conversorJson.writeValueAsString("Usuario no encontrado");
                    } else if (ejemplar == null) {
                        json_response = conversorJson.writeValueAsString("Ejemplar no encontrado");
                    } else {
                        Prestamo prestamoToAdd = new Prestamo();
                        prestamoToAdd.setUsuario(usuario);
                        prestamoToAdd.setEjemplar(ejemplar);
                        prestamoToAdd.setFechaInicio(fechaInicio);

                        boolean addResult = daoPrestamo.add(prestamoToAdd);

                        json_response = conversorJson.writeValueAsString(
                                addResult ? "Préstamo agregado correctamente" : "Error al agregar préstamo"
                        );
                    }
                    break;

                case "update":
                    Integer idToUpdate = Integer.parseInt(request.getParameter("id"));
                    LocalDate nuevaFechaDevolucion = LocalDate.parse(request.getParameter("fechaDevolucion"));

                    Prestamo prestamoToUpdate = daoPrestamo.getById(idToUpdate);
                    if (prestamoToUpdate != null) {
                        prestamoToUpdate.setFechaDevolucion(nuevaFechaDevolucion);
                        Prestamo updatedPrestamo = daoPrestamo.update(prestamoToUpdate);

                        json_response = conversorJson.writeValueAsString(
                                updatedPrestamo != null ? updatedPrestamo : "Error al actualizar préstamo"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Préstamo no encontrado");
                    }
                    break;

                case "delete":
                    Integer idToDelete = Integer.parseInt(request.getParameter("id"));

                    Prestamo prestamoToDelete = daoPrestamo.getById(idToDelete);
                    if (prestamoToDelete != null) {
                        boolean deleteResult = daoPrestamo.delete(prestamoToDelete);

                        json_response = conversorJson.writeValueAsString(
                                deleteResult ? "Préstamo eliminado correctamente" : "Error al eliminar préstamo"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Préstamo no encontrado");
                    }
                    break;

                case "select":
                    Integer idToSelect = Integer.parseInt(request.getParameter("id"));
                    Prestamo prestamo = daoPrestamo.getById(idToSelect);

                    json_response = conversorJson.writeValueAsString(prestamo != null ? prestamo : "Préstamo no encontrado");
                    break;

                default:
                    json_response = conversorJson.writeValueAsString("Acción no válida");
                    break;
            }
        } catch (Exception e) {
            json_response = conversorJson.writeValueAsString("Error al procesar la acción: " + e.getMessage());
            System.err.println("Error en doPost: " + e.getMessage());
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
            System.err.println("Error en doGet: " + e.getMessage());
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//@WebServlet(name = "PrestamoServlet", value = "/PrestamoServlet")
//public class PrestamoServlet extends HttpServlet {
//
//    private DAOGenerico<Prestamo, Integer> daoPrestamo;
//    private DAOGenerico<Usuario, Integer> daoUsuario;
//    private DAOGenerico<Ejemplar, Integer> daoEjemplar;
//    private controladorPrestamo controladorPrestamo;
//
//    @Override
//    public void init() throws ServletException {
//        try {
//            System.out.println("Inicializando PrestamoServlet...");
//            daoPrestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
//            daoUsuario = new DAOGenerico<>(Usuario.class, Integer.class);
//            daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
//            controladorPrestamo = new controladorPrestamo();
//            System.out.println("PrestamoServlet inicializado correctamente.");
//        } catch (Exception e) {
//            System.err.println("Error al inicializar DAOGenerico: " + e.getMessage());
//            throw new ServletException("Error al inicializar DAOGenerico", e);
//        }
//    }
//
//    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        PrintWriter impresora = response.getWriter();
//        ObjectMapper conversorJson = new ObjectMapper();
//        conversorJson.registerModule(new JavaTimeModule());
//
//        String action = request.getParameter("action");
//        String json_response = "";
//
//        try {
//            switch (action) {
//                case "add": // Registrar un préstamo
//                    Integer usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
//                    Integer ejemplarId = Integer.parseInt(request.getParameter("ejemplarId"));
//
//                    Usuario usuario = daoUsuario.getById(usuarioId);
//                    Ejemplar ejemplar = daoEjemplar.getById(ejemplarId);
//
//                    try {
//                        // Validaciones
//                        controladorPrestamo.validarRegistrarPrestamo(usuario, ejemplar);
//
//                        // Preparar el préstamo
//                        Prestamo prestamo = new Prestamo();
//                        controladorPrestamo.prepararPrestamo(prestamo, usuario, ejemplar);
//
//                        // Persistencia
//                        boolean resultado = daoPrestamo.add(prestamo);
//
//                        json_response = conversorJson.writeValueAsString(
//                                resultado ? "Préstamo registrado correctamente." : "Error al registrar el préstamo."
//                        );
//                    } catch (Exception e) {
//                        json_response = conversorJson.writeValueAsString("Error: " + e.getMessage());
//                    }
//                    break;
//
//                case "return": // Registrar una devolución
//                    Integer prestamoId = Integer.parseInt(request.getParameter("prestamoId"));
//
//                    Prestamo prestamo = daoPrestamo.getById(prestamoId);
//
//                    try {
//                        // Preparar la devolución
//                        controladorPrestamo.prepararDevolucion(prestamo);
//
//                        // Persistencia
//                        Prestamo updatedPrestamo = daoPrestamo.update(prestamo);
//
//                        json_response = conversorJson.writeValueAsString(
//                                updatedPrestamo != null ? "Devolución registrada correctamente." : "Error al registrar la devolución."
//                        );
//                    } catch (Exception e) {
//                        json_response = conversorJson.writeValueAsString("Error: " + e.getMessage());
//                    }
//                    break;
//
//                default:
//                    json_response = conversorJson.writeValueAsString("Acción no válida.");
//                    break;
//            }
//        } catch (Exception e) {
//            json_response = conversorJson.writeValueAsString("Error al procesar la solicitud: " + e.getMessage());
//        }
//
//        impresora.println(json_response);
//    }
//
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        PrintWriter impresora = response.getWriter();
//        ObjectMapper conversorJson = new ObjectMapper();
//        conversorJson.registerModule(new JavaTimeModule());
//
//        try {
//            List<Prestamo> listaPrestamos = daoPrestamo.getAll();
//            String json_response = conversorJson.writeValueAsString(listaPrestamos);
//            impresora.println(json_response);
//        } catch (Exception e) {
//            String errorJson = conversorJson.writeValueAsString("Error al obtener los préstamos: " + e.getMessage());
//            impresora.println(errorJson);
//        }
//    }
//
//    @Override
//    public void destroy() {
//        try {
//            if (daoPrestamo != null) daoPrestamo.close();
//            if (daoUsuario != null) daoUsuario.close();
//            if (daoEjemplar != null) daoEjemplar.close();
//        } catch (Exception e) {
//            System.err.println("Error al liberar recursos de DAO: " + e.getMessage());
//        }
//    }
//}

