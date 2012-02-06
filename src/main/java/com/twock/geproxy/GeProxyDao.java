package com.twock.geproxy;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.entity.Player;
import org.apache.log4j.Logger;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeProxyDao {
  private static final Logger log = Logger.getLogger(GeProxyDao.class);
  private final EntityManager entityManager;

  @Inject
  public GeProxyDao(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public void updatePlanets(List<Planet> planets) {
    Query deleteBySystem = entityManager.createNamedQuery("deleteBySystem");
    deleteBySystem.setParameter("galaxy", planets.get(0).getCoordinate().getGalaxy());
    deleteBySystem.setParameter("system", planets.get(0).getCoordinate().getSystem());
    deleteBySystem.executeUpdate();

    for(Planet planet : planets) {
      if(planet.getPlayer() != null) {
        Player player = entityManager.find(Player.class, planet.getPlayer().getName());
        if(player == null) {
          entityManager.persist(planet.getPlayer());
        }
      }
      entityManager.persist(planet);
    }
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public List<Planet> getPlanetsInSystem(int galaxy, int system) {
    Query findBySystem = entityManager.createNamedQuery("findBySystem");
    findBySystem.setParameter("galaxy", galaxy);
    findBySystem.setParameter("system", system);
    return (List<Planet>)findBySystem.getResultList();
  }
}
