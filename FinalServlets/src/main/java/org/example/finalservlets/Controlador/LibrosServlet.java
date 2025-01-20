package org.example.finalservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.finalservlets.Modelo.DAOGenerico;
import org.example.finalservlets.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "LibrosServlet", value = "/LibrosServlet")
public class LibrosServlet extends HttpServlet {

    private DAOGenerico<Libro, String> daolibro;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Inicializando LibrosServlet...");
            daolibro = new DAOGenerico<>(Libro.class, String.class);
            System.out.println("DAOGenerico inicializado correctamente.");
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
                    String titulo = request.getParameter("titulo");
                    String autor = request.getParameter("autor");

                    Libro libroToAdd = new Libro(isbn, titulo, autor);
                    boolean addResult = daolibro.add(libroToAdd);

                    json_response = conversorJson.writeValueAsString(
                            addResult ? "Libro agregado correctamente" : "Error al agregar libro"
                    );
                    break;

                case "update":
                    String isbnToUpdate = request.getParameter("isbn");
                    String newTitulo = request.getParameter("titulo");
                    String newAutor = request.getParameter("autor");

                    Libro libroToUpdate = new Libro(isbnToUpdate, newTitulo, newAutor);
                    Libro updatedLibro = daolibro.update(libroToUpdate);

                    json_response = conversorJson.writeValueAsString(
                            updatedLibro != null ? updatedLibro : "Error al actualizar libro"
                    );
                    break;

                case "delete":
                    String isbnToDelete = request.getParameter("isbn");

                    Libro libroToDelete = daolibro.getById(isbnToDelete);
                    if (libroToDelete != null) {
                        boolean deleteResult = daolibro.delete(libroToDelete);

                        json_response = conversorJson.writeValueAsString(
                                deleteResult ? "Libro eliminado correctamente" : "Error al eliminar libro"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Libro no encontrado");
                    }
                    break;

                case "select":
                    String isbnToSelect = request.getParameter("isbn");
                    Libro libro = daolibro.getById(isbnToSelect);

                    json_response = conversorJson.writeValueAsString(libro != null ? libro : "Libro no encontrado");
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
            List<Libro> listaLibros = daolibro.getAll();
            String json_response = conversorJson.writeValueAsString(listaLibros);
            impresora.println(json_response);
        } catch (Exception e) {
            String errorJson = conversorJson.writeValueAsString("Error al obtener los libros: " + e.getMessage());
            impresora.println(errorJson);
            System.err.println("Error en doGet: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (daolibro != null) {
                daolibro.close(); // Si DAO tiene recursos que liberar
            }
        } catch (Exception e) {
            System.err.println("Error al liberar recursos de DAO: " + e.getMessage());
        }
    }
}
