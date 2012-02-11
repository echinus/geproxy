package com.twock.geproxy.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Embeddable
public class Coordinate implements Serializable {
  private int galaxy;
  private int system;
  private int planet;

  public Coordinate() {
  }

  public Coordinate(int galaxy, int system, int planet) {
    this.galaxy = galaxy;
    this.system = system;
    this.planet = planet;
  }

  public int getGalaxy() {
    return galaxy;
  }

  public void setGalaxy(int galaxy) {
    this.galaxy = galaxy;
  }

  public int getSystem() {
    return system;
  }

  public void setSystem(int system) {
    this.system = system;
  }

  public int getPlanet() {
    return planet;
  }

  public void setPlanet(int planet) {
    this.planet = planet;
  }

  public static Coordinate fromString(String coordinateText) {
    String[] split = coordinateText.split(":");
    if(split.length != 3) {
      throw new IllegalArgumentException("Invalid coordinate " + coordinateText);
    }
    return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
  }

  @Override
  public String toString() {
    return galaxy + ":" + system + ":" + planet;
  }
}
