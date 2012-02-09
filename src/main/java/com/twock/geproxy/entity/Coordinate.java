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
  private int row;

  public Coordinate() {
  }

  public Coordinate(int galaxy, int system, int row) {
    this.galaxy = galaxy;
    this.system = system;
    this.row = row;
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

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
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
    return galaxy + ":" + system + ":" + row;
  }
}
