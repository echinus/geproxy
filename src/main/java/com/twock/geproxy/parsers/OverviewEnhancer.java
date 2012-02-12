package com.twock.geproxy.parsers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.twock.geproxy.GeProxyDao;
import com.twock.geproxy.entity.*;
import freemarker.template.Configuration;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public class OverviewEnhancer {
  private static final Logger log = LoggerFactory.getLogger(OverviewEnhancer.class);
  private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder().appendDays().appendSeparator("d").appendHours().appendSeparator("h").appendMinutes().appendSeparator("m").appendSeconds().appendSeparatorIfFieldsBefore("s").toFormatter();
  private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendDayOfWeekShortText().appendLiteral(' ').appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2).toFormatter();
  private static final Pattern PATTERN = Pattern.compile("(<p>[^<]*?\\[(\\d+:\\d+:\\d+)\\]<br/>.*?</li>)", Pattern.DOTALL);
  private static final Pattern COORD = Pattern.compile("\\[(\\d+:\\d+:\\d+)\\]");
  private final Configuration freemarkerConfiguration;
  private final GeProxyDao geProxyDao;

  @Inject
  public OverviewEnhancer(Configuration freemarkerConfiguration, GeProxyDao geProxyDao) {
    this.freemarkerConfiguration = freemarkerConfiguration;
    this.geProxyDao = geProxyDao;
  }

  public String enhanceOverview(String html) throws Exception {
    geProxyDao.finishFleetMovements();
    List<FleetMovement> fleetMovements = geProxyDao.getAllFleetMovements();

    Matcher coordMatcher = COORD.matcher(html);
    coordMatcher.find();
    Coordinate thisCoordinate = Coordinate.fromString(coordMatcher.group(1), PlanetTypeEnum.PLANET);
    List<FleetMovement> movementsForThisPlanet = getFleetsDestinedFor(fleetMovements, thisCoordinate);
    html = html.replaceFirst("</li>", "</li><li>" + getNewText(movementsForThisPlanet, geProxyDao.getFleetsAtPlanet(thisCoordinate)) + "</li>");

    Matcher matcher = PATTERN.matcher(html);
    StringBuffer sb = new StringBuffer();
    while(matcher.find()) {
      Coordinate coordinate = Coordinate.fromString(matcher.group(2), PlanetTypeEnum.PLANET);
      movementsForThisPlanet = getFleetsDestinedFor(fleetMovements, coordinate);
      List<Fleet> fleetsAtPlanet = geProxyDao.getFleetsAtPlanet(coordinate);

      if(movementsForThisPlanet.isEmpty() && fleetsAtPlanet.isEmpty()) {
        matcher.appendReplacement(sb, matcher.group());
      } else {
        log.debug("Replacing with " + matcher.group() + "<li>" + getNewText(movementsForThisPlanet, fleetsAtPlanet) + "</li>");
        matcher.appendReplacement(sb, matcher.group() + "<li>" + getNewText(movementsForThisPlanet, fleetsAtPlanet) + "</li>");
      }
    }
    matcher.appendTail(sb);
    return sb.toString();

//    StringWriter stringWriter = new StringWriter();
//    Map<String, Object> map = new HashMap<String, Object>();
//    map.put("fleetMovements", fleetMovements);
//    freemarkerConfiguration.getTemplate("overview.ftl").process(map, stringWriter);
//    String replaced = html.replace("</ul>", stringWriter.toString() + "</ul>");
//    log.debug("New html: " + replaced);
//    return replaced;
  }

  private String getNewText(List<FleetMovement> movementsForThisPlanet, List<Fleet> fleets) {
    StringBuilder sb = new StringBuilder();
    for(Iterator<FleetMovement> i = movementsForThisPlanet.iterator(); i.hasNext(); ) {
      FleetMovement movement = i.next();
      sb.append(movement.getMission().text);
      if(!PlanetTypeEnum.PLANET.equals(movement.getEventualDestination().getPlanetType())) {
        sb.append(" to ").append(movement.getEventualDestination().getPlanetType().text.toLowerCase());
      }
      sb.append(movement.getMission().returns ? " via " : " from ");
      sb.append(movement.getMission().returns ? movement.getTo() : movement.getFrom());
      sb.append(" in ").append(periodFormatter.print(new Period(DateTime.now(), movement.getTimeOfEventualArrival())));
      sb.append(" (").append(dateTimeFormatter.print(movement.getTimeOfEventualArrival())).append(')');
      sb.append(": ").append(getShortString(movement.getShips()));
      if(i.hasNext() || !fleets.isEmpty()) {
        sb.append("<br/>");
      }
    }
    for(Iterator<Fleet> i = fleets.iterator(); i.hasNext(); ) {
      Fleet fleet = i.next();
      sb.append("Ships at ").append(fleet.getCoordinate().getPlanetType().text.toLowerCase());
      sb.append(": ").append(getShortString(fleet.getShips()));
      if(i.hasNext()) {
        sb.append("<br/>");
      }
    }
    log.info("New text: " + sb.toString());
    return sb.toString();
  }

  private String getShortString(Map<ShipTypeEnum, Integer> ships) {
    StringBuilder sb = new StringBuilder();
    for(Iterator<Map.Entry<ShipTypeEnum, Integer>> i = ships.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry<ShipTypeEnum, Integer> entry = i.next();
      sb.append(entry.getValue()).append(entry.getKey().shortText);
      if(i.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  private List<FleetMovement> getFleetsDestinedFor(List<FleetMovement> fleetMovements, Coordinate coordinate) {
    List<FleetMovement> result = new ArrayList<FleetMovement>();
    for(FleetMovement fleetMovement : fleetMovements) {
      Coordinate target = fleetMovement.getEventualDestination();
      if(target.getGalaxy() == coordinate.getGalaxy() && target.getSystem() == coordinate.getSystem() && target.getPlanet() == coordinate.getPlanet()) {
        result.add(fleetMovement);
      }
    }
    Collections.sort(result, new Comparator<FleetMovement>() {
      @Override
      public int compare(FleetMovement o1, FleetMovement o2) {
        return o1.getTimeOfEventualArrival().compareTo(o2.getTimeOfEventualArrival());
      }
    });
    return result;
  }
}
