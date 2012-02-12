package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum ShipTypeEnum {
  SMALL_CARGO("Small Cargo", "SC", 202),
  LARGE_CARGO("Large Cargo", "LC", 203),
  LIGHT_FIGHTER("Light Fighter", "LF", 204),
  HEAVY_FIGHTER("Heavy Fighter", "HF", 205),
  CRUISER("Cruiser", "C", 206),
  BATTLESHIP("Battleship", "BS", 207),
  COLONY_SHIP("Colony Ship", "CS", 208),
  RECYCLER("Recycler", "R", 209),
  ESPIONAGE_PROBE("Espionage Probe", "EP", 210),
  BOMBER("Bomber", "B", 211),
  SOLAR_SATELLITE("Solar Satellite", "SS", 212),
  DEATHSTAR("Deathstar", "DS", 214),
  BATTLECRUISER("Battlecruiser", "BC", 215);

  public final String name;
  public final String shortText;
  public final int id;

  private ShipTypeEnum(String name, String shortText, int id) {
    this.name = name;
    this.shortText = shortText;
    this.id = id;
  }

  public static ShipTypeEnum fromName(String name) {
    for(ShipTypeEnum shipTypeEnum : values()) {
      if(shipTypeEnum.name.equals(name)) {
        return shipTypeEnum;
      }
    }
    throw new IllegalArgumentException("Invalid ship type " + name);
  }
}
