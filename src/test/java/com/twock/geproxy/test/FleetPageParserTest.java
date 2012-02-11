package com.twock.geproxy.test;

import javax.xml.xpath.XPathFactory;

import com.twock.geproxy.entity.Fleet;
import com.twock.geproxy.entity.ShipTypeEnum;
import com.twock.geproxy.parsers.FleetPageParser;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class FleetPageParserTest {
  @Test
  public void parseDeploy() throws Exception {
    String response = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet1.html"));
    Fleet fleet = new FleetPageParser(XPathFactory.newInstance()).parse(response);
    Assert.assertEquals(fleet.getCoordinate().getGalaxy(), 1);
    Assert.assertEquals(fleet.getCoordinate().getSystem(), 326);
    Assert.assertEquals(fleet.getCoordinate().getPlanet(), 10);
    Assert.assertEquals(fleet.getShips().size(), 3, fleet.getShips().toString());
    Assert.assertEquals(fleet.getShips().get(ShipTypeEnum.SMALL_CARGO).intValue(), 174);
    Assert.assertEquals(fleet.getShips().get(ShipTypeEnum.LARGE_CARGO).intValue(), 513);
    Assert.assertEquals(fleet.getShips().get(ShipTypeEnum.ESPIONAGE_PROBE).intValue(), 33);
  }
}
