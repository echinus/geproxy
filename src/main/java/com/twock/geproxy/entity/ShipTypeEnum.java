package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum ShipTypeEnum {
  SMALL_CARGO("Small Cargo", 202),
  LARGE_CARGO("Large Cargo", 203),
  LIGHT_FIGHTER("Light Fighter", 204),
  HEAVY_FIGHTER("Heavy Fighter", 205),
  CRUISER("Cruiser", 206),
  BATTLESHIP("Battleship", 207),
  COLONY_SHIP("Colony Ship", 208),
  RECYCLER("Recycler", 209),
  ESPIONAGE_PROBE("Espionage Probe", 210),
  BOMBER("Bomber", 211),
  SOLAR_SATELLITE("Solar Satellite", 212),
  DEATHSTAR("Deathstar", 214),
  BATTLECRUISER("Battlecruiser", 215);

  public final String name;
  public final int id;

  private ShipTypeEnum(String name, int id) {
    this.name = name;
    this.id = id;
  }
}
