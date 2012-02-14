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
    Coordinate viewingPlanet = null;
    Set<Coordinate> allPlanets = new HashSet<Coordinate>();
    while(coordMatcher.find()) {
      Coordinate thisCoordinate = Coordinate.fromString(coordMatcher.group(1), PlanetTypeEnum.PLANET);
      allPlanets.add(thisCoordinate);
      if(viewingPlanet == null) {
        viewingPlanet = thisCoordinate;
      }
    }
    List<FleetMovement> movementsForThisPlanet = getFleetsDestinedFor(fleetMovements, viewingPlanet, allPlanets);
    html = html.replaceFirst("</li>", "</li><li>" + getNewText(viewingPlanet, movementsForThisPlanet, geProxyDao.getFleetsAtPlanet(viewingPlanet)) + "</li>");

    Matcher matcher = PATTERN.matcher(html);
    StringBuffer sb = new StringBuffer();
    while(matcher.find()) {
      Coordinate coordinate = Coordinate.fromString(matcher.group(2), PlanetTypeEnum.PLANET);
      movementsForThisPlanet = getFleetsDestinedFor(fleetMovements, coordinate, allPlanets);
      List<Fleet> fleetsAtPlanet = geProxyDao.getFleetsAtPlanet(coordinate);

      if(movementsForThisPlanet.isEmpty() && fleetsAtPlanet.isEmpty()) {
        matcher.appendReplacement(sb, matcher.group());
      } else {
        matcher.appendReplacement(sb, matcher.group() + "<li>" + getNewText(coordinate, movementsForThisPlanet, fleetsAtPlanet) + "</li>");
      }
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private String getNewText(Coordinate thisPlanet, List<FleetMovement> movementsForThisPlanet, List<Fleet> fleets) {
    StringBuilder sb = new StringBuilder();
    for(Iterator<FleetMovement> i = movementsForThisPlanet.iterator(); i.hasNext(); ) {
      FleetMovement movement = i.next();
      sb.append(movement.getMission().text);
      if(!PlanetTypeEnum.PLANET.equals(movement.getEventualDestination().getPlanetType())) {
        sb.append(" to ").append(movement.getEventualDestination().getPlanetType().text.toLowerCase());
      }
      sb.append(movement.getMission().returns ? " via " : " from ");
      sb.append(movement.getMission().returns ? movement.getTo() : movement.getFrom());
      if(!samePlanet(movement.getEventualDestination(), thisPlanet)) {
        sb.append(" to ").append(movement.getEventualDestination());
      }
      DateTime now = DateTime.now();
      sb.append(" in ").append(periodFormatter.print(new Period(now, movement.getTimeOfEventualArrival())));
      sb.append(" (").append(dateTimeFormatter.print(movement.getTimeOfEventualArrival())).append(')');
      if(now.isBefore(movement.getEta())) {
        sb.append(" [return ").append(dateTimeFormatter.print(now.plus(new Period(movement.getStartTime(), now)))).append(']');
      }
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
    log.debug("New text: " + sb.toString());
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

  private List<FleetMovement> getFleetsDestinedFor(List<FleetMovement> fleetMovements, Coordinate coordinate, Set<Coordinate> allPlanets) {
    List<FleetMovement> result = new ArrayList<FleetMovement>();
    for(FleetMovement fleetMovement : fleetMovements) {
      Coordinate target = isAnOwnPlanet(fleetMovement.getEventualDestination(), allPlanets) ? fleetMovement.getEventualDestination() : fleetMovement.getFrom();
      if(samePlanet(coordinate, target)) {
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

  private boolean samePlanet(Coordinate coordinate, Coordinate target) {
    return target.getGalaxy() == coordinate.getGalaxy() && target.getSystem() == coordinate.getSystem() && target.getPlanet() == coordinate.getPlanet();
  }

  private boolean isAnOwnPlanet(Coordinate eventualDestination, Set<Coordinate> allPlanets) {
    for(Coordinate check : allPlanets) {
      if(samePlanet(eventualDestination, check)) {
        return true;
      }
    }
    return false;
  }
}
