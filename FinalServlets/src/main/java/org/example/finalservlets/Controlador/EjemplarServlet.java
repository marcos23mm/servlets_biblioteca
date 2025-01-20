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
import org.example.finalservlets.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "EjemplarServlet", value = "/EjemplarServlet")
public class EjemplarServlet extends HttpServlet {

    private DAOGenerico<Ejemplar, Integer> daoEjemplar;
    private DAOGenerico<Libro, String> daoLibro;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Inicializando EjemplarServlet...");
            daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
            daoLibro = new DAOGenerico<>(Libro.class, String.class);
            System.out.println("DAOGenerico para Ejemplar y Libro inicializado correctamente.");
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
                    String isbn = request.getParameter("isbn");
                    String estado = request.getParameter("estado");

                    Libro libro = daoLibro.getById(isbn);
                    if (libro == null) {
                        json_response = conversorJson.writeValueAsString("Libro no encontrado para el ISBN proporcionado");
                    } else {
                        Ejemplar ejemplarToAdd = new Ejemplar(libro, estado != null ? estado : "Disponible");
                        boolean addResult = daoEjemplar.add(ejemplarToAdd);

                        json_response = conversorJson.writeValueAsString(
                                addResult ? "Ejemplar agregado correctamente" : "Error al agregar ejemplar"
                        );
                    }
                    break;

                case "update":
                    Integer idToUpdate = Integer.parseInt(request.getParameter("id"));
                    String newEstado = request.getParameter("estado");

                    Ejemplar ejemplarToUpdate = daoEjemplar.getById(idToUpdate);
                    if (ejemplarToUpdate != null) {
                        ejemplarToUpdate.setEstado(newEstado != null ? newEstado : ejemplarToUpdate.getEstado());
                        Ejemplar updatedEjemplar = daoEjemplar.update(ejemplarToUpdate);

                        json_response = conversorJson.writeValueAsString(
                                updatedEjemplar != null ? updatedEjemplar : "Error al actualizar ejemplar"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Ejemplar no encontrado");
                    }
                    break;

                case "delete":
                    Integer idToDelete = Integer.parseInt(request.getParameter("id"));

                    Ejemplar ejemplarToDelete = daoEjemplar.getById(idToDelete);
                    if (ejemplarToDelete != null) {
                        boolean deleteResult = daoEjemplar.delete(ejemplarToDelete);

                        json_response = conversorJson.writeValueAsString(
                                deleteResult ? "Ejemplar eliminado correctamente" : "Error al eliminar ejemplar"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Ejemplar no encontrado");
                    }
                    break;

                case "select":
                    Integer idToSelect = Integer.parseInt(request.getParameter("id"));
                    Ejemplar ejemplar = daoEjemplar.getById(idToSelect);

                    json_response = conversorJson.writeValueAsString(ejemplar != null ? ejemplar : "Ejemplar no encontrado");
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
            List<Ejemplar> listaEjemplares = daoEjemplar.getAll();
            String json_response = conversorJson.writeValueAsString(listaEjemplares);
            impresora.println(json_response);
        } catch (Exception e) {
            String errorJson = conversorJson.writeValueAsString("Error al obtener los ejemplares: " + e.getMessage());
            impresora.println(errorJson);
            System.err.println("Error en doGet: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (daoEjemplar != null) daoEjemplar.close();
            if (daoLibro != null) daoLibro.close();
        } catch (Exception e) {
            System.err.println("Error al liberar recursos de DAO: " + e.getMessage());
        }
    }
}

