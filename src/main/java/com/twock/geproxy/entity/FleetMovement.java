package com.twock.geproxy.entity;

import java.util.Map;
import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Entity
public class FleetMovement {
  private long id;
  private Map<ShipTypeEnum, Integer> ships;
  private DateTime startTime;
  private DateTime eta;
  private DateTime returnTime;

  public FleetMovement() {
  }

  public FleetMovement(long id, Map<ShipTypeEnum, Integer> ships, DateTime startTime, DateTime eta, DateTime returnTime) {
    this.id = id;
    this.ships = ships;
    this.startTime = startTime;
    this.eta = eta;
    this.returnTime = returnTime;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ElementCollection
  @MapKeyEnumerated(EnumType.STRING)
  public Map<ShipTypeEnum, Integer> getShips() {
    return ships;
  }

  public void setShips(Map<ShipTypeEnum, Integer> ships) {
    this.ships = ships;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(DateTime startTime) {
    this.startTime = startTime;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getEta() {
    return eta;
  }

  public void setEta(DateTime eta) {
    this.eta = eta;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getReturnTime() {
    return returnTime;
  }

  public void setReturnTime(DateTime returnTime) {
    this.returnTime = returnTime;
  }
}
