package com.twock.geproxy.parsers;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.*;

import com.google.inject.Inject;
import com.twock.geproxy.entity.*;
import org.cyberneko.html.parsers.DOMParser;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class Fleet3PageParser {
  private static final DateTimeFormatter TIME_PARSER = new DateTimeFormatterBuilder().appendMonthOfYearShortText().appendLiteral(' ').appendDayOfWeekShortText().appendLiteral(' ').appendDayOfMonth(2).appendLiteral(' ').appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2).toFormatter();
  private static final Logger log = LoggerFactory.getLogger(Fleet3PageParser.class);
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

  public FleetMovement parse(String responseBody) throws Exception {
    DOMParser parser = new DOMParser();
    parser.parse(new InputSource(new StringReader(responseBody)));
    Document document = parser.getDocument();
    XPath path = xPath.get();
    MissionEnum mission = MissionEnum.fromText((String)path.evaluate("//TR[TH[1]='Mission']/TH[2]", document, XPathConstants.STRING));
    DateTime arrivalTime = TIME_PARSER.parseDateTime((String)path.evaluate("//TR[TH[1]='Arrival time']/TH[2]", document, XPathConstants.STRING));
    DateTime returnTime = TIME_PARSER.parseDateTime((String)path.evaluate("//TR[TH[1]='Return time']/TH[2]", document, XPathConstants.STRING));
    Coordinate from = Coordinate.fromString((String)path.evaluate("//TR[TH[1]='From']/TH[2]", document, XPathConstants.STRING));
    Coordinate destination = Coordinate.fromString((String)path.evaluate("//TR[TH[1]='Destination']/TH[2]", document, XPathConstants.STRING));
    if(returnTime.compareTo(arrivalTime) < 0) {
      // in case it's the end of december, we'll always parse with this year
      returnTime.minusYears(1);
    }
    Period travelTime = new Period(arrivalTime, returnTime);

    Map<ShipTypeEnum, Integer> ships = new HashMap<ShipTypeEnum, Integer>();
    NodeList fleetRows = (NodeList)path.evaluate("//LI[node()[1]='Fleet:']/TABLE/TBODY/TR", document, XPathConstants.NODESET);
    for(int i = 0; i < fleetRows.getLength(); i++) {
      NodeList childNodes = fleetRows.item(i).getChildNodes();
      ShipTypeEnum shipType = ShipTypeEnum.fromName(childNodes.item(0).getTextContent());
      Integer count = Integer.valueOf(childNodes.item(1).getTextContent());
      ships.put(shipType, count);
    }

    DateTime actualStartTime = new DateTime();
    DateTime actualEta = actualStartTime.plus(travelTime);
    DateTime actualReturnTime = mission.returns ? actualEta.plus(travelTime) : null;
    FleetMovement result = new FleetMovement(mission, ships, from, destination, actualStartTime, actualEta, actualReturnTime);
    log.debug("Parsed " + result.toString());
    return result;
  }
}
