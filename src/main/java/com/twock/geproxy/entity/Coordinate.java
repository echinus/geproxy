package com.twock.geproxy.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Embeddable
public class Coordinate implements Serializable {
  private int galaxy;
  private int system;
  private int planet;
  private PlanetTypeEnum planetType;

  public Coordinate() {
  }

  public Coordinate(int galaxy, int system, int planet, PlanetTypeEnum planetType) {
    this.galaxy = galaxy;
    this.system = system;
    this.planet = planet;
    this.planetType = planetType;
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

  @Enumerated(EnumType.STRING)
  @Column(length = 15)
  public PlanetTypeEnum getPlanetType() {
    return planetType;
  }

  public void setPlanetType(PlanetTypeEnum planetType) {
    this.planetType = planetType;
  }

  public static Coordinate fromString(String coordinateText, PlanetTypeEnum planetType) {
    String[] split = coordinateText.split(":");
    if(split.length != 3) {
      throw new IllegalArgumentException("Invalid coordinate " + coordinateText);
    }
    return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), planetType);
  }

  @Override
  public String toString() {
    return galaxy + ":" + system + ":" + planet + "(" + planetType.name() + ")";
  }
}
