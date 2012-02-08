package com.twock.geproxy.test;

import java.util.List;

import com.google.inject.Injector;
import com.twock.geproxy.GEProxy;
import com.twock.geproxy.GalaxyPageParser;
import com.twock.geproxy.GeProxyDao;
import com.twock.geproxy.entity.Planet;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class DaoTest {
  private static final Logger log = Logger.getLogger(DaoTest.class);
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
}
