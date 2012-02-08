package com.twock.geproxy.test;

import javax.xml.xpath.XPathFactory;

import com.twock.geproxy.Fleet3PageParser;
import com.twock.geproxy.entity.FleetMovement;
import com.twock.geproxy.entity.ShipTypeEnum;
import org.apache.commons.io.IOUtils;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.joda.time.Period;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class Fleet3PageParserTest {
  @Test
  public void parseDeploy() throws Exception {
    QueryStringDecoder request = new QueryStringDecoder("/?" + IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet3deployrequestbody.html")));
    String response = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fleet3deploy.html"));
    FleetMovement fleetMovement = new Fleet3PageParser(XPathFactory.newInstance()).parse(request.getParameters(), response);

    Assert.assertEquals(fleetMovement.getShips().get(ShipTypeEnum.SMALL_CARGO), Integer.valueOf(1));
    Assert.assertEquals(new Period(fleetMovement.getStartTime(), fleetMovement.getEta()).toStandardSeconds(), 78);
    Assert.assertEquals(new Period(fleetMovement.getEta(), fleetMovement.getReturnTime()).toStandardSeconds(), 78);
  }
}
