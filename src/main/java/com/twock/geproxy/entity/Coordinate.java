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

  @Override
  public String toString() {
    return galaxy + ":" + system + ":" + row;
  }
}
