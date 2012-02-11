package com.twock.geproxy.test;

import java.util.List;
import javax.xml.xpath.XPathFactory;

import com.google.inject.Injector;
import com.twock.geproxy.GEProxy;
import com.twock.geproxy.GeProxyDao;
import com.twock.geproxy.entity.Fleet;
import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.parsers.FleetPageParser;
import com.twock.geproxy.parsers.GalaxyPageParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class DaoTest {
  private static final Logger log = LoggerFactory.getLogger(DaoTest.class);
  private GalaxyPageParser galaxyPageParser;
  private GeProxyDao geProxyDao;

  @BeforeClass
  protected void setUp() throws Exception {
    Injector injector = GEProxy.createInjector();
    galaxyPageParser = injector.getInstance(GalaxyPageParser.class);
    geProxyDao = injector.getInstance(GeProxyDao.class);
  }

  @Test
  public void savePlanets() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy.html"));
    List<Planet> planets = galaxyPageParser.parse(galaxy);
    geProxyDao.updatePlanets(planets);
  }

  @Test(dependsOnMethods = "savePlanets")
  public void checkSaved() {
    List<Planet> planetsInSystem = geProxyDao.getPlanetsInSystem(1, 324);
    Assert.assertEquals(planetsInSystem.size(), 15);
  }

  @Test(dependsOnMethods = "checkSaved")
  public void updateSameGalaxy() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy.html"));
    List<Planet> planets = galaxyPageParser.parse(galaxy);
    geProxyDao.updatePlanets(planets);
  }

  @Test(dependsOnMethods = "savePlanets")
  public void checkUpdated() {
    List<Planet> planetsInSystem = geProxyDao.getPlanetsInSystem(1, 324);
    Assert.assertEquals(planetsInSystem.size(), 15);
    for(Planet planet : planetsInSystem) {
      log.info("found: " + planet);
    }
  }

  @Test
  public void persistFleet() throws Exception {
    String response = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet1.html"));
    FleetPageParser fleetPageParser = new FleetPageParser(XPathFactory.newInstance());
    for(int i = 0; i < 2; i++) {
      Fleet fleet = fleetPageParser.parse(response);
      geProxyDao.updateFleet(fleet);
    }
  }
}
