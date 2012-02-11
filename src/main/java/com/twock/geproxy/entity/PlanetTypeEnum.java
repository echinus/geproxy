package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum PlanetTypeEnum {
  PLANET(1),
  DEBRIS_FIELD(2),
  MOON(3);

  public final int id;

  private PlanetTypeEnum(int id) {
    this.id = id;
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
