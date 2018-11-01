package com.bri64.discord.fortbot.json;

public class Division {

  private Stat trnRating;
  private Stat score;
  private Stat top1;
  private Stat top3;
  private Stat top5;
  private Stat top6;
  private Stat top10;
  private Stat top12;
  private Stat top25;
  private Stat kd;
  private Stat winRatio;
  private Stat matches;
  private Stat kills;
  private Stat kpg;
  private Stat scorePerMatch;

  public Division() {
  }

  public Stat getTrnRating() {
    return trnRating;
  }

  public Stat getScore() {
    return score;
  }

  public Stat getTop1() {
    return top1;
  }

  public Stat getTop3() {
    return top3;
  }

  public Stat getTop5() {
    return top5;
  }

  public Stat getTop6() {
    return top6;
  }

  public Stat getTop10() {
    return top10;
  }

  public Stat getTop12() {
    return top12;
  }

  public Stat getTop25() {
    return top25;
  }

  public Stat getKd() {
    return kd;
  }

  public Stat getWinRatio() {
    return winRatio;
  }

  public Stat getMatches() {
    return matches;
  }

  public Stat getKills() {
    return kills;
  }

  public Stat getKpg() {
    return kpg;
  }

  public Stat getScorePerMatch() {
    return scorePerMatch;
  }

  @Override
  public String toString() {
    return top1.toString() + "\n" +
        matches.toString() + "\n" +
        kd.toString();
  }
}
