package com.twock.geproxy.test;

import java.util.List;
import javax.xml.xpath.XPathFactory;

import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.parsers.GalaxyPageParser;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GalaxyPageParserTest {
  private List<Planet> galaxy1;

  @Test
  public void testParse() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy.html"));
    galaxy1 = new GalaxyPageParser(XPathFactory.newInstance()).parse(galaxy);
  }

  @Test(dependsOnMethods = "testParse")
  public void testInactiveParsing() {
    Assert.assertTrue(galaxy1.get(5).getPlayer().isInactive(), galaxy1.get(5).toString());
  }

  @Test(dependsOnMethods = "testParse")
  public void testNoActivity() {
    Assert.assertNull(galaxy1.get(9).getLastActivity(), galaxy1.get(9).toString());
  }

  @Test(dependsOnMethods = "testParse")
  public void testNoPlanet() {
    Assert.assertNull(galaxy1.get(0).getLastActivity());
  }

  @Test
  public void testParse2() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy2.html"));
    new GalaxyPageParser(XPathFactory.newInstance()).parse(galaxy);
  }

  @Test
  public void testParse3() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy3.html"));
    new GalaxyPageParser(XPathFactory.newInstance()).parse(galaxy);
  }
}
