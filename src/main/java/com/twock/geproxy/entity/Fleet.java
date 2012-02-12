package com.twock.geproxy.entity;

import java.util.Map;
import javax.persistence.*;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Entity
@org.hibernate.annotations.Table(
  appliesTo = "Fleet",
  indexes = {
    @Index(name = "fleet_coordinate", columnNames = {"galaxy", "system", "planet"})
  }
)
@NamedQueries({
  @NamedQuery(name = "findByPlanet", query = "select f from Fleet f where f.coordinate.galaxy=:galaxy and f.coordinate.system=:system and f.coordinate.planet=:planet")
})
public class Fleet {
  private Coordinate coordinate;
  private DateTime shipsLastUpdated;
  private Map<ShipTypeEnum, Integer> ships;

  public Fleet() {
  }

  public Fleet(Coordinate coordinate, DateTime shipsLastUpdated, Map<ShipTypeEnum, Integer> ships) {
    this.coordinate = coordinate;
    this.shipsLastUpdated = shipsLastUpdated;
    this.ships = ships;
  }

  @EmbeddedId
  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getShipsLastUpdated() {
    return shipsLastUpdated;
  }

  public void setShipsLastUpdated(DateTime shipsLastUpdated) {
    this.shipsLastUpdated = shipsLastUpdated;
  }

  @ElementCollection
  @MapKeyEnumerated(EnumType.STRING)
  @MapKeyColumn(length = 20)
  public Map<ShipTypeEnum, Integer> getShips() {
    return ships;
  }

  public void setShips(Map<ShipTypeEnum, Integer> ships) {
    this.ships = ships;
  }

  @Override
  public String toString() {
    return "Fleet{" +
      "coordinate=" + coordinate +
      ", shipsLastUpdated=" + shipsLastUpdated +
      ", ships=" + ships +
      '}';
  }
}
