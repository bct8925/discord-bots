package com.bri64.discord.fortbot.json;

public class Stat {

  private String label;
  private String field;
  private String category;
  private int valueInt;
  private String value;
  private int rank;
  private float percentile;
  private String displayValue;

  public Stat() {
  }

  public String getLabel() {
    return label;
  }

  public String getField() {
    return field;
  }

  public String getCategory() {
    return category;
  }

  public int getValueInt() {
    return valueInt;
  }

  public String getValue() {
    return value;
  }

  public int getRank() {
    return rank;
  }

  public float getPercentile() {
    return percentile;
  }

  public String getDisplayValue() {
    return displayValue;
  }

  @Override
  public String toString() {
    return label + ": " + displayValue;
  }
}
