package com.twock.geproxy;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.twock.geproxy.entity.*;
import org.joda.time.DateTime;
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

  @Transactional
  public void updateFleet(Fleet fleet) {
    finishFleetMovements();
    EntityManager entityManager = entityManagerProvider.get();
    Fleet existingFleet = entityManager.find(Fleet.class, fleet.getCoordinate());
    if(existingFleet != null) {
      entityManager.remove(existingFleet);
    }
    entityManager.persist(fleet);
  }

  @Transactional
  public void finishFleetMovements() {
    EntityManager entityManager = entityManagerProvider.get();
    @SuppressWarnings("unchecked")
    List<FleetMovement> fleetMovements = entityManager.createQuery("from FleetMovement").getResultList();
    for(FleetMovement fleetMovement : fleetMovements) {
      if(fleetMovement.getMission().returns && fleetMovement.getReturnTime().isBeforeNow()) {
        addShips(fleetMovement.getShips(), fleetMovement.getFrom(), fleetMovement.getReturnTime());
        entityManager.remove(fleetMovement);
      } else if(!fleetMovement.getMission().returns && fleetMovement.getEta().isBeforeNow()) {
        addShips(fleetMovement.getShips(), fleetMovement.getTo(), fleetMovement.getEta());
        entityManager.remove(fleetMovement);
      }
    }
  }

  private void addShips(Map<ShipTypeEnum, Integer> ships, Coordinate coordinate, DateTime timestamp) {
    EntityManager entityManager = entityManagerProvider.get();
    Fleet fleet = entityManager.find(Fleet.class, coordinate);
    if(fleet == null) {
      entityManager.persist(new Fleet(coordinate, timestamp, ships));
    } else {
      for(Map.Entry<ShipTypeEnum, Integer> entry : ships.entrySet()) {
        fleet.getShips().put(entry.getKey(), fleet.getShips().containsKey(entry.getKey()) ? fleet.getShips().get(entry.getKey()) + entry.getValue() : entry.getValue());
      }
      entityManager.flush();
    }
  }

  @SuppressWarnings("unchecked")
  public List<FleetMovement> getAllFleetMovements() {
    return entityManagerProvider.get().createQuery("from FleetMovement").getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Fleet> getFleetsAtPlanet(Coordinate coordinate) {
    TypedQuery<Fleet> findByPlanet = entityManagerProvider.get().createNamedQuery("findByPlanet", Fleet.class);
    findByPlanet.setParameter("galaxy", coordinate.getGalaxy());
    findByPlanet.setParameter("system", coordinate.getSystem());
    findByPlanet.setParameter("planet", coordinate.getPlanet());
    return findByPlanet.getResultList();
  }
}
