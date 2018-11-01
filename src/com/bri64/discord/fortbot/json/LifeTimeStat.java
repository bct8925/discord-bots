package com.bri64.discord.fortbot.json;


@SuppressWarnings("WeakerAccess")
public class LifeTimeStat {

  private String key;
  private String value;

  public LifeTimeStat() {
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return key + ": " + value;
  }
}
