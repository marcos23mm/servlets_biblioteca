package org.example.finalservlets.Modelo;

import jakarta.persistence.*;
import java.util.List;

public class DAOGenerico<T, ID> {
    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;
    private Class<T> clase;
    private Class<ID> claseID;

    public DAOGenerico(Class<T> clase, Class<ID> claseID) {
        try {
            this.clase = clase;
            this.claseID = claseID;
            // Inicialización del EntityManagerFactory
            emf = Persistence.createEntityManagerFactory("unidad-biblioteca");
            em = emf.createEntityManager();
            tx = em.getTransaction();
            System.out.println("DAOGenerico inicializado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar DAOGenerico: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar DAOGenerico", e);
        }
    }

    // INSERT
    public boolean add(T objeto) {
        try {
            tx.begin();
            em.persist(objeto);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public T getById(ID id) {
        try {
            System.out.println("Buscando entidad con ID: " + id);
            T entity = em.find(clase, id);
            if (entity == null) {
                System.err.println("No se encontró la entidad con ID: " + id);
            } else {
                System.out.println("Entidad encontrada: " + entity.toString());
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // SELECT *
    public List<T> getAll() {
        try {
            return em.createQuery("SELECT c FROM " + clase.getName() + " c", clase).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public T update(T objeto) {
        try {
            tx.begin();
            if (!em.contains(objeto)) {
                objeto = em.merge(objeto);
            }
            tx.commit();
            return objeto;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    // DELETE
    public boolean delete(T objeto) {
        try {
            tx.begin();
            em.remove(em.contains(objeto) ? objeto : em.merge(objeto));
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    // Método para cerrar recursos
    public void close() {
        try {
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
            System.out.println("Recursos liberados correctamente.");
        } catch (Exception e) {
            System.err.println("Error al liberar recursos: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "DAOGenerico{" +
                "clase=" + clase.getSimpleName() +
                ", claseID=" + claseID.getSimpleName() +
                '}';
    }
}
