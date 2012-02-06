package com.twock.geproxy.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * @author Chris Pearson (chris@twock.com)
 */
@Entity
public class Player implements Serializable {
  private String name;
  private DateTime lastUpdated;
  private int rank;
  private boolean inactive;

  public Player() {
  }

  public Player(String name, DateTime lastUpdated, int rank, boolean inactive) {
    this.name = name;
    this.lastUpdated = lastUpdated;
    this.rank = rank;
    this.inactive = inactive;
  }

  @Id
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(DateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public boolean isInactive() {
    return inactive;
  }

  public void setInactive(boolean inactive) {
    this.inactive = inactive;
  }

  @Override
  public String toString() {
    return "Player{" +
      "name='" + name + '\'' +
      ", lastUpdated=" + lastUpdated +
      ", rank=" + rank +
      ", inactive=" + inactive +
      '}';
  }
}
