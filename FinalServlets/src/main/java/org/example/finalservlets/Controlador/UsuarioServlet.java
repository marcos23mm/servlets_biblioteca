package org.example.finalservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.finalservlets.Modelo.DAOGenerico;
import org.example.finalservlets.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "UsuarioServlet", value = "/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    private DAOGenerico<Usuario, Integer> daoUsuario;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Inicializando UsuarioServlet...");
            daoUsuario = new DAOGenerico<>(Usuario.class, Integer.class);
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
                    String dni = request.getParameter("dni");
                    String nombre = request.getParameter("nombre");
                    String email = request.getParameter("email");
                    String password = request.getParameter("password");
                    String tipo = request.getParameter("tipo");

                    Usuario usuarioToAdd = new Usuario();
                    usuarioToAdd.setDni(dni);
                    usuarioToAdd.setNombre(nombre);
                    usuarioToAdd.setEmail(email);
                    usuarioToAdd.setPassword(password);
                    usuarioToAdd.setTipo(tipo);

                    boolean addResult = daoUsuario.add(usuarioToAdd);

                    json_response = conversorJson.writeValueAsString(
                            addResult ? "Usuario agregado correctamente" : "Error al agregar usuario"
                    );
                    break;

                case "update":
                    Integer idToUpdate = Integer.parseInt(request.getParameter("id"));
                    String newNombre = request.getParameter("nombre");
                    String newEmail = request.getParameter("email");
                    String newPassword = request.getParameter("password");
                    String newTipo = request.getParameter("tipo");
                    LocalDate penalizacion = request.getParameter("penalizacionHasta") != null
                            ? LocalDate.parse(request.getParameter("penalizacionHasta"))
                            : null;

                    Usuario usuarioToUpdate = daoUsuario.getById(idToUpdate);
                    if (usuarioToUpdate != null) {
                        usuarioToUpdate.setNombre(newNombre);
                        usuarioToUpdate.setEmail(newEmail);
                        usuarioToUpdate.setPassword(newPassword);
                        usuarioToUpdate.setTipo(newTipo);
                        usuarioToUpdate.setPenalizacionHasta(penalizacion);

                        Usuario updatedUsuario = daoUsuario.update(usuarioToUpdate);
                        json_response = conversorJson.writeValueAsString(
                                updatedUsuario != null ? updatedUsuario : "Error al actualizar usuario"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Usuario no encontrado");
                    }
                    break;

                case "delete":
                    Integer idToDelete = Integer.parseInt(request.getParameter("id"));

                    Usuario usuarioToDelete = daoUsuario.getById(idToDelete);
                    if (usuarioToDelete != null) {
                        boolean deleteResult = daoUsuario.delete(usuarioToDelete);

                        json_response = conversorJson.writeValueAsString(
                                deleteResult ? "Usuario eliminado correctamente" : "Error al eliminar usuario"
                        );
                    } else {
                        json_response = conversorJson.writeValueAsString("Usuario no encontrado");
                    }
                    break;

                case "select":
                    Integer idToSelect = Integer.parseInt(request.getParameter("id"));
                    Usuario usuario = daoUsuario.getById(idToSelect);

                    json_response = conversorJson.writeValueAsString(usuario != null ? usuario : "Usuario no encontrado");
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
            List<Usuario> listaUsuarios = daoUsuario.getAll();
            String json_response = conversorJson.writeValueAsString(listaUsuarios);
            impresora.println(json_response);
        } catch (Exception e) {
            String errorJson = conversorJson.writeValueAsString("Error al obtener los usuarios: " + e.getMessage());
            impresora.println(errorJson);
            System.err.println("Error en doGet: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (daoUsuario != null) {
                daoUsuario.close();
            }
        } catch (Exception e) {
            System.err.println("Error al liberar recursos de DAO: " + e.getMessage());
        }
    }
}

