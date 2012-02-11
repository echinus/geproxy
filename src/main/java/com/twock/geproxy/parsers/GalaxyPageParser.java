package com.twock.geproxy.parsers;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.*;

import com.google.inject.Inject;
import com.twock.geproxy.entity.Coordinate;
import com.twock.geproxy.entity.Planet;
import com.twock.geproxy.entity.Player;
import org.cyberneko.html.parsers.DOMParser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class GalaxyPageParser {
  private static final Logger log = LoggerFactory.getLogger(GalaxyPageParser.class);
  private static final Pattern RANK_MATCHER = Pattern.compile("\\( ranked (\\d+)\\)");
  private static final Pattern ACTIVITY_MATCHER = Pattern.compile("\\((\\d+)min \\)");
  private static final Pattern DEBRIS_MATCHER = Pattern.compile("(\\d+(?:\\.\\d+)?)([MK]?) / (\\d+(?:\\.\\d+)?)([MK]?)");
  private final ThreadLocal<XPath> xPath;

  @Inject
  public GalaxyPageParser(final XPathFactory xPathFactory) throws XPathExpressionException {
    this.xPath = new ThreadLocal<XPath>() {
      @Override
      protected XPath initialValue() {
        return xPathFactory.newXPath();
      }
    };
  }

  public List<Planet> parse(String responseBody) throws Exception {
    DateTime now = new DateTime();
    List<Planet> planets = new ArrayList<Planet>();
    Map<String, Player> players = new HashMap<String, Player>();
    DOMParser parser = new DOMParser();
    parser.parse(new InputSource(new StringReader(responseBody)));
    Document document = parser.getDocument();
    XPath path = xPath.get();
    NodeList numberText = (NodeList)path.evaluate("//DIV[@class='in']/INPUT/@value", document, XPathConstants.NODESET);
    int galaxyNumber = Integer.parseInt(numberText.item(0).getTextContent());
    int systemNumber = Integer.parseInt(numberText.item(1).getTextContent());

    NodeList galaxyRowsList = (NodeList)path.evaluate("//DIV[@class='galaxy_row']", document, XPathConstants.NODESET);
    for(int i = 0; i < galaxyRowsList.getLength(); i++) {
      Node galaxyRow = galaxyRowsList.item(i);
      int rowNumber = Integer.valueOf((String)path.evaluate("DIV[@class='gn']/text()", galaxyRow, XPathConstants.STRING));
      NodeList occupierParts = (NodeList)path.evaluate("DIV[@class='g_right']", galaxyRow, XPathConstants.NODESET);

      if(occupierParts.getLength() > 0) {
        boolean inactive = ((NodeList)path.evaluate("P/FONT[@class='inactive']", occupierParts.item(0), XPathConstants.NODESET)).getLength() > 0;
        NodeList whoParts = (NodeList)path.evaluate("P//text()", occupierParts.item(0), XPathConstants.NODESET);
        String who = joinTextContents(whoParts, " ");
        String name = who.substring(0, who.indexOf('(')).trim();

        Matcher matcher = RANK_MATCHER.matcher(who);
        int rank = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;

        matcher = ACTIVITY_MATCHER.matcher(who);
        String activityTime = matcher.find() ? matcher.group(1) : null;
        if(activityTime == null && who.contains("(*)")) {
          activityTime = "*";
        }

        String debris = (String)path.evaluate("P/FONT/text()[starts-with(.,'Debris')]", occupierParts.item(0), XPathConstants.STRING);
        matcher = DEBRIS_MATCHER.matcher(debris);
        int debrisMetal = 0;
        int debrisCrystal = 0;
        if(matcher.find()) {
          debrisMetal = new BigDecimal(matcher.group(1)).scaleByPowerOfTen("K".equals(matcher.group(2)) ? 3 : ("M".equals(matcher.group(2)) ? 6 : 0)).intValue();
          debrisCrystal = new BigDecimal(matcher.group(3)).scaleByPowerOfTen("K".equals(matcher.group(4)) ? 3 : ("M".equals(matcher.group(4)) ? 6 : 0)).intValue();
        }

        String planetName = (String)path.evaluate("P/BR/following-sibling::text()[1]", occupierParts.item(0), XPathConstants.STRING);

        Player player = players.get(name);
        if(player == null) {
          player = new Player(name, now, rank, inactive);
          players.put(name, player);
        }
        DateTime activityTimestamp = activityTime == null ? null : (activityTime.equals("*") ? now : now.minusMinutes(Integer.parseInt(activityTime)));
        Planet newPlanet = new Planet(new Coordinate(galaxyNumber, systemNumber, rowNumber), now, player, planetName, debrisMetal, debrisCrystal, activityTimestamp);
        log.debug(newPlanet.toString());
        planets.add(newPlanet);
      } else {
        Planet newPlanet = new Planet(new Coordinate(galaxyNumber, systemNumber, rowNumber), now, null, null, 0, 0, null);
        log.debug(newPlanet.toString());
        planets.add(newPlanet);
      }
    }
    return planets;
  }

  private String joinTextContents(NodeList nodeList, String separator) {
    StringBuilder sb = new StringBuilder();
    for(int j = 0; j < nodeList.getLength() - 1; j++) {
      if(j != 0) {
        sb.append(separator);
      }
      sb.append(nodeList.item(j).getTextContent());
    }
    return sb.toString();
  }
}
