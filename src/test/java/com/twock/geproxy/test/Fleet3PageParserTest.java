package com.twock.geproxy.test;

import javax.xml.xpath.XPathFactory;

import com.google.inject.Injector;
import com.twock.geproxy.GEProxy;
import com.twock.geproxy.GeProxyDao;
import com.twock.geproxy.entity.FleetMovement;
import com.twock.geproxy.entity.ShipTypeEnum;
import com.twock.geproxy.parsers.Fleet3PageParser;
import org.apache.commons.io.IOUtils;
import org.joda.time.Period;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class Fleet3PageParserTest {
  private FleetMovement fleetMovement;

  @Test
  public void parseDeploy() throws Exception {
    String response = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet3deploy.html"));
    fleetMovement = new Fleet3PageParser(XPathFactory.newInstance()).parse(response, IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet3deployrequestbody.html")));

    Assert.assertEquals(fleetMovement.getShips().get(ShipTypeEnum.SMALL_CARGO), Integer.valueOf(1));
    Assert.assertEquals(new Period(fleetMovement.getStartTime(), fleetMovement.getEta()).toStandardSeconds().getSeconds(), 78);
    Assert.assertNull(fleetMovement.getReturnTime());
  }

  @Test(dependsOnMethods = "parseDeploy")
  public void persist() {
    Injector injector = GEProxy.createInjector(8080);
    GeProxyDao dao = injector.getInstance(GeProxyDao.class);
    dao.addFleetMovement(fleetMovement);
  }
}
