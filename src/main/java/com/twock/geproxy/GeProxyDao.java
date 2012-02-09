package com.twock.geproxy;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.twock.geproxy.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GeProxyDao {
  private static final Logger log = LoggerFactory.getLogger(GeProxyDao.class);
  private final Provider<EntityManager> entityManagerProvider;

  @Inject
  public GeProxyDao(Provider<EntityManager> entityManagerProvider) {
    this.entityManagerProvider = entityManagerProvider;
  }

  @Transactional
  public void updatePlanets(List<Planet> planets) {
    EntityManager entityManager = entityManagerProvider.get();
    Coordinate coordinate = planets.get(0).getCoordinate();
    for(Planet planet : getPlanetsInSystem(coordinate.getGalaxy(), coordinate.getSystem())) {
      entityManager.remove(planet);
    }
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
    Query findBySystem = entityManagerProvider.get().createNamedQuery("findBySystem");
    findBySystem.setParameter("galaxy", galaxy);
    findBySystem.setParameter("system", system);
    return (List<Planet>)findBySystem.getResultList();
  }

  @Transactional
  public void addFleetMovement(FleetMovement fleetMovement) {
    entityManagerProvider.get().persist(fleetMovement);
  }
}
