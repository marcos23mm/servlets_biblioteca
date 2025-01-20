//package org.example.finalservlets.Controlador;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mysql.cj.x.protobuf.MysqlxExpr;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.example.finalservlets.Modelo.DAOGenerico;
//
//import java.io.PrintWriter;
//
//
//@WebServlet(name = "pruebaServlet", value = "/pruebaServlet")
//public class Servlet extends HttpServlet {
//
//    private DAOGenerico<Tabla, PrimaryKey> daoPrueba;
//
//    @Override
//    public void init() throws ServletException {
//        try{
//            System.out.println("Iniciando Servlet");
//            daoPrueba = new DAOGenerico<>(Tabla.class, PrimaryKey.class);
//            System.out.println("Servlet iniciado");
//        }catch (Exception e){
//            System.out.println("Error al iniciar el Servlet: ");
//            throw new ServletException(e);
//        }
//    }
//
//
//    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
//        response.setContentType("application/json/");
//        PrintWriter impresora = response.getWriter();
//        ObjectMapper conversorJson = new ObjectMapper();
//        conversorJson.registerModule(new JavaTimeModule());
//
//        String action = request.getParameter("action");
//        String json_response = "";
//
//        try{
//            switch (action) {
//                case "insertar":
//                    String dato1 = request.getParameter("dato1");
//                    String dato2 = request.getParameter("dato2");
//                    String dato3 = request.getParameter("dato3");
//                    //etc
//                    //Al ser una inserccion, vamos a crear un objeto
//
//                    Tabla tablaToAdd = new Tabla();
//                    tablaToAdd.setDato1(dato1); //En este caso me da el error porque no tengo el modelo de Tabla debido a que es un ejemplo.
//                    tablaToAdd.setDato2(dato2);
//                    tablaToAdd.setDato3(dato3);
//
//                    boolean addResult = daoPrueba.add(tablaToAdd);
//
//                    json_response = conversorJson.writeValueAsString(
//                            addResult ? "Usuario agregado correctamente" : "Error al agregar usuario"
//                    );
//                    break;
//
//
//                    case "actualizar":
//                        Integer id = Integer.parseInt(request.getParameter("id"));
//                        String newDato1 = request.getParameter("dato1");
//                        String newDato2 = request.getParameter("dato2");
//                        String newDato3 = request.getParameter("dato3");
//
//                        Tabla tablaToUpdate = daoPrueba.getById(id);
//
//                        if (tablaToUpdate != null) {
//                            tablaToUpdate.setDato1(newDato1);
//                            tablaToUpdate.setDato2(newDato2);
//                            tablaToUpdate.setDato2(newDato3);
//
//                            Tabla tablaActualizada = daoPrueba.update(tablaToUpdate);
//
//                            json_response = conversorJson.writeValueAsString(
//                                    tablaActualizada != null ? "Usuario actualizado correctamente" : "Error al actualizar usuario"
//                            );
//
//                        }else {
//                            json_response = conversorJson.writeValueAsString("Tabla no encontrada");
//
//                        }
//                        break;
//
//
//                    case "eliminar":
//                        Integer idElimminar = Integer.parseInt(request.getParameter("id"));
//
//                        Tabla tablaToDelete = daoPrueba.getById(idElimminar);
//                        if (tablaToDelete != null) {
//                            boolean eliminarResult = daoPrueba.delete(tablaToDelete);
//
//                            json_response = conversorJson.writeValueAsString(
//                                    eliminarResult ? "Usuario eliminado correctamente" : "Error al eliminar usuario"
//                            );
//                        }else{
//                            json_response = conversorJson.writeValueAsString("Tabla no encontrada");
//                        }
//                        break;
//
//                    case "seleccionar":
//                        Integer idSeleccionar = Integer.parseInt(request.getParameter("id"));
//                        Tabla tabla = daoPrueba.getById(idSeleccionar);
//
//                        json_response = conversorJson.writeValueAsString(tabla != null);
//                        break;
//
//                    default:
//                        json_response = conversorJson.writeValueAsString("Accion no valida");
////                        break;
//
//
//            }
//
//        }catch (Exception e){
//            json_response = conversorJson.writeValueAsString("Error al procesar" + e.getMessage());
//            e.printStackTrace();
//        }
//
//        impresora.print(json_response);
//
//    }
//
//}
