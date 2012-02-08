package com.twock.geproxy;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.google.inject.Inject;
import com.twock.geproxy.entity.FleetMovement;
import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.entity.ShipTypeEnum;
import org.cyberneko.html.parsers.DOMParser;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class Fleet3PageParser {
  private final ThreadLocal<XPath> xPath;

  @Inject
  public Fleet3PageParser(final XPathFactory xPathFactory) throws XPathExpressionException {
    this.xPath = new ThreadLocal<XPath>() {
      @Override
      protected XPath initialValue() {
        return xPathFactory.newXPath();
      }
    };
  }

  public FleetMovement parse(Map<String, List<String>> requestBody, String responseBody) throws Exception {
    DOMParser parser = new DOMParser();
    parser.parse(new InputSource(new StringReader(responseBody)));
    Document document = parser.getDocument();
    XPath path = xPath.get();
//    path.evaluate("//ul[@class='plastic']")

    Map<ShipTypeEnum, Integer> ships = new HashMap<ShipTypeEnum, Integer>();
    DateTime startTime = new DateTime();
    DateTime eta = new DateTime();
    DateTime returnTime = new DateTime();
    return new FleetMovement(0, ships, startTime, eta, returnTime);
  }
}
