package com.twock.geproxy.parsers;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.google.inject.Inject;
import com.twock.geproxy.entity.Coordinate;
import com.twock.geproxy.entity.Fleet;
import com.twock.geproxy.entity.ShipTypeEnum;
import org.cyberneko.html.parsers.DOMParser;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class FleetPageParser {
  private final ThreadLocal<XPath> xPath;

  @Inject
  public FleetPageParser(final XPathFactory xPathFactory) {
    this.xPath = new ThreadLocal<XPath>() {
      @Override
      protected XPath initialValue() {
        return xPathFactory.newXPath();
      }
    };
  }

  public Fleet parse(String responseBody) throws Exception {
    DOMParser parser = new DOMParser();
    parser.parse(new InputSource(new StringReader(responseBody)));
    Document document = parser.getDocument();
    XPath path = xPath.get();

    int galaxy = Integer.parseInt((String)path.evaluate("//INPUT[@name='galaxy']/@value", document, XPathConstants.STRING));
    int system = Integer.parseInt((String)path.evaluate("//INPUT[@name='system']/@value", document, XPathConstants.STRING));
    int planet = Integer.parseInt((String)path.evaluate("//INPUT[@name='planet']/@value", document, XPathConstants.STRING));

    NodeList fleetRows = (NodeList)path.evaluate("//FORM[@id='fleet0']/TABLE/TBODY/TR[position()>=3 and position()<=last()-3]", document, XPathConstants.NODESET);
    Map<ShipTypeEnum, Integer> ships = new HashMap<ShipTypeEnum, Integer>(fleetRows.getLength());
    for(int i = 0; i < fleetRows.getLength(); i++) {
      Node row = fleetRows.item(i);
      ShipTypeEnum shipType = ShipTypeEnum.fromName((String)path.evaluate("TD[1]/node()[1]", row, XPathConstants.STRING));
      Integer count = Integer.valueOf((String)path.evaluate("TD[2]/SMALL/text()", row, XPathConstants.STRING));
      ships.put(shipType, count);
    }
    return new Fleet(new Coordinate(galaxy, system, planet), new DateTime(), ships);
  }
}
