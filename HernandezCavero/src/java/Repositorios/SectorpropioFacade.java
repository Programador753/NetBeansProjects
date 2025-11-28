/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Repositorios;

import Entidades.Sectorpropio;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author anton
 */
@Stateless
public class SectorpropioFacade extends AbstractFacade<Sectorpropio> {

    @PersistenceContext(unitName = "HernandezCaveroPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SectorpropioFacade() {
        super(Sectorpropio.class);
    }
    
}
