package com.twock.geproxy.test;

import java.util.List;
import javax.xml.xpath.XPathFactory;

import com.google.inject.Injector;
import com.twock.geproxy.GEProxy;
import com.twock.geproxy.GalaxyPageParser;
import com.twock.geproxy.GeProxyDao;
import com.twock.geproxy.entity.Planet;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GalaxyPageParserTest {
  private static final Logger log = Logger.getLogger(GalaxyPageParserTest.class);

  @Test
  public void testParse() throws Exception {
    String galaxy = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("galaxy.html"));
    new GalaxyPageParser(XPathFactory.newInstance()).parse(galaxy);
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
