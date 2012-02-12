package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum PlanetTypeEnum {
  PLANET(1, "Planet"),
  DEBRIS_FIELD(2, "Debris Field"),
  MOON(3, "Moon");

  public final int id;
  public final String text;

  private PlanetTypeEnum(int id, String text) {
    this.id = id;
    this.text = text;
  }

  public static PlanetTypeEnum fromId(int id) {
    for(PlanetTypeEnum planetTypeEnum : values()) {
      if(planetTypeEnum.id == id) {
        return planetTypeEnum;
      }
    }
    throw new IllegalArgumentException("Unknown type ID " + id);
  }
}
