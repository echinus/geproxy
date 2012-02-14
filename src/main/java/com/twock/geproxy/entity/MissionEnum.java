package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum MissionEnum {
  ATTACK(1, "Attack", true),
  TRANSPORT(3, "Transport", true),
  DEPLOY(4, "Deploy", false),
  HOLD_POSITION(5, "Hold Position", true),
  COLONIZE(7, "Colonize", false),
  RECYCLE(8, "Recycle", true);

  public final int id;
  public final String text;
  public final boolean returns;

  private MissionEnum(int id, String text, boolean returns) {
    this.id = id;
    this.text = text;
    this.returns = returns;
  }

  public static MissionEnum fromText(String text) {
    for(MissionEnum missionEnum : values()) {
      if(missionEnum.text.equals(text)) {
        return missionEnum;
      }
    }
    throw new IllegalArgumentException("Unknown mission text " + text);
  }
}
