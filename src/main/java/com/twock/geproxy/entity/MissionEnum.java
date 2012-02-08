package com.twock.geproxy.entity;

/**
 * @author Chris Pearson (chris@twock.com)
 */
public enum MissionEnum {
  ATTACK(1),
  DEPLOY(4),
  RECYCLE(8);

  public final int id;

  private MissionEnum(int id) {
    this.id = id;
  }
}
