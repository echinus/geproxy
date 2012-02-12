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
  appliesTo = "FleetMovement",
  indexes = {
    @Index(name = "fleetmovement_from", columnNames = {"from_galaxy", "from_system", "from_planet", "from_planettype"}),
    @Index(name = "fleetmovement_to", columnNames = {"to_galaxy", "to_system", "to_planet", "to_planettype"})
  }
)
public class FleetMovement {
  private long id;
  private MissionEnum mission;
  private Map<ShipTypeEnum, Integer> ships;
  private Coordinate from;
  private Coordinate to;
  private DateTime startTime;
  private DateTime eta;
  private DateTime returnTime;

  public FleetMovement() {
  }

  public FleetMovement(MissionEnum mission, Map<ShipTypeEnum, Integer> ships, Coordinate from, Coordinate to, DateTime startTime, DateTime eta, DateTime returnTime) {
    this.mission = mission;
    this.ships = ships;
    this.from = from;
    this.to = to;
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

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  public MissionEnum getMission() {
    return mission;
  }

  public void setMission(MissionEnum mission) {
    this.mission = mission;
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

  @SuppressWarnings("JpaDataSourceORMInspection")
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "galaxy", column = @Column(name = "from_galaxy")),
    @AttributeOverride(name = "system", column = @Column(name = "from_system")),
    @AttributeOverride(name = "planet", column = @Column(name = "from_planet")),
    @AttributeOverride(name = "planetType", column = @Column(name = "from_planettype"))
  })
  public Coordinate getFrom() {
    return from;
  }

  public void setFrom(Coordinate from) {
    this.from = from;
  }

  @SuppressWarnings("JpaDataSourceORMInspection")
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "galaxy", column = @Column(name = "to_galaxy")),
    @AttributeOverride(name = "system", column = @Column(name = "to_system")),
    @AttributeOverride(name = "planet", column = @Column(name = "to_planet")),
    @AttributeOverride(name = "planetType", column = @Column(name = "to_planettype"))
  })
  public Coordinate getTo() {
    return to;
  }

  public void setTo(Coordinate to) {
    this.to = to;
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

  @Transient
  public Coordinate getEventualDestination() {
    return mission.returns ? getFrom() : getTo();
  }

  @Transient
  public DateTime getTimeOfEventualArrival() {
    return mission.returns ? getReturnTime() : getEta();
  }

  @Override
  public String toString() {
    return "FleetMovement{" +
      "id=" + id +
      ", mission=" + mission +
      ", ships=" + ships +
      ", from=" + from +
      ", to=" + to +
      ", startTime=" + startTime +
      ", eta=" + eta +
      ", returnTime=" + returnTime +
      '}';
  }
}
