/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Repositorios;

import Entidades.Inspectoria;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author anton
 */
@Stateless
public class InspectoriaFacade extends AbstractFacade<Inspectoria> {

    @PersistenceContext(unitName = "HernandezCaveroPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InspectoriaFacade() {
        super(Inspectoria.class);
    }
    
    /**
     * Busca inspectorías por código de socio local
     * @param socioLocal código del socio local
     * @return Lista de inspectorías del socio local
     */
    public List<Inspectoria> findBySocioLocal(String socioLocal) {
        return em.createNamedQuery("Inspectoria.findBySocioLocal", Inspectoria.class)
                .setParameter("socioLocal", socioLocal)
                .getResultList();
    }
    
}
