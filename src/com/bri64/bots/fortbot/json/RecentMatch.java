package com.bri64.bots.fortbot.json;


public class RecentMatch {

  private int id;
  private String accountId;
  private String playlist;
  private int kills;
  private int minutesPlayed;
  private int top1;
  private int top5;
  private int top6;
  private int top10;
  private int top12;
  private int top25;
  private int matches;
  private int top3;
  private String dateCollected;
  private int score;
  private int platform;
  private float trnRating;
  private float trnRatingChange;

  public RecentMatch() {
  }

  public int getId() {
    return id;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getPlaylist() {
    return playlist;
  }

  public int getKills() {
    return kills;
  }

  public int getMinutesPlayed() {
    return minutesPlayed;
  }

  public int getTop1() {
    return top1;
  }

  public int getTop5() {
    return top5;
  }

  public int getTop6() {
    return top6;
  }

  public int getTop10() {
    return top10;
  }

  public int getTop12() {
    return top12;
  }

  public int getTop25() {
    return top25;
  }

  public int getMatches() {
    return matches;
  }

  public int getTop3() {
    return top3;
  }

  public String getDateCollected() {
    return dateCollected;
  }

  public int getScore() {
    return score;
  }

  public int getPlatform() {
    return platform;
  }

  public float getTrnRating() {
    return trnRating;
  }

  public float getTrnRatingChange() {
    return trnRatingChange;
  }

}