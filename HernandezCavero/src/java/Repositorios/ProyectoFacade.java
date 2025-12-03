/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Repositorios;

import Entidades.Inspectoria;
import Entidades.Proyecto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author anton
 */
@Stateless
public class ProyectoFacade extends AbstractFacade<Proyecto> {

    @PersistenceContext(unitName = "HernandezCaveroPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectoFacade() {
        super(Proyecto.class);
    }
    
    /**
     * Busca proyectos por inspectoría usando NamedQuery
     * @param inspectoria la inspectoría a buscar
     * @return Lista de proyectos de esa inspectoría
     */
    public List<Proyecto> findByInspectoria(Inspectoria inspectoria) {
        return em.createNamedQuery("Proyecto.findByInspectoria", Proyecto.class)
                .setParameter("inspectoria", inspectoria)
                .getResultList();
    }
    
    /**
     * Busca proyectos por código de status usando NamedQuery
     * @param codStatus código del status (Integer)
     * @return Lista de proyectos con ese status
     */
    public List<Proyecto> findByStatus(Integer codStatus) {
        return em.createNamedQuery("Proyecto.findByStatus", Proyecto.class)
                .setParameter("status", codStatus)
                .getResultList();
    }
    
    /**
     * Busca proyectos por inspectoría y status usando NamedQuery
     * @param inspectoria la inspectoría a buscar
     * @param codStatus código del status (Integer)
     * @return Lista de proyectos que cumplen ambos filtros
     */
    public List<Proyecto> findByInspectoriaAndStatus(Inspectoria inspectoria, Integer codStatus) {
        return em.createNamedQuery("Proyecto.findByInspectoriaAndStatus", Proyecto.class)
                .setParameter("inspectoria", inspectoria)
                .setParameter("status", codStatus)
                .getResultList();
    }
    
}
