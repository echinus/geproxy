package com.twock.geproxy.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "findBySystem", query = "select p from Planet p where p.coordinate.galaxy=:galaxy and p.coordinate.system=:system")
})
public class Planet implements Serializable {
  private Coordinate coordinate;
  private DateTime lastUpdated;
  private Player player;
  private String planetName;
  private int debrisMetal;
  private int debrisCrystal;
  private DateTime lastActivity;

  public Planet() {
  }

  public Planet(Coordinate coordinate, DateTime lastUpdated, Player player, String planetName, int debrisMetal, int debrisCrystal, DateTime lastActivity) {
    this.coordinate = coordinate;
    this.lastUpdated = lastUpdated;
    this.player = player;
    this.planetName = planetName;
    this.debrisMetal = debrisMetal;
    this.debrisCrystal = debrisCrystal;
    this.lastActivity = lastActivity;
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
  public DateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(DateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @ManyToOne
  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public String getPlanetName() {
    return planetName;
  }

  public void setPlanetName(String planetName) {
    this.planetName = planetName;
  }

  public int getDebrisMetal() {
    return debrisMetal;
  }

  public void setDebrisMetal(int debrisMetal) {
    this.debrisMetal = debrisMetal;
  }

  public int getDebrisCrystal() {
    return debrisCrystal;
  }

  public void setDebrisCrystal(int debrisCrystal) {
    this.debrisCrystal = debrisCrystal;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getLastActivity() {
    return lastActivity;
  }

  public void setLastActivity(DateTime lastActivity) {
    this.lastActivity = lastActivity;
  }

  @Override
  public String toString() {
    return "Planet{" +
      "coordinate=" + coordinate +
      ", lastUpdated=" + lastUpdated +
      ", player=" + player +
      ", planet=" + (planetName == null ? "null" : "'" + planetName + '\'') +
      ", debrisMetal=" + debrisMetal +
      ", debrisCrystal=" + debrisCrystal +
      ", lastActivity=" + lastActivity +
      '}';
  }
}
