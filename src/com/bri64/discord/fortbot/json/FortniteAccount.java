package com.bri64.discord.fortbot.json;

import java.util.List;

public class FortniteAccount {

  private String accountId;
  private int platformId;
  private String platformName;
  private String platformNameLong;
  private String epicUserHandle;
  private DivisionStats stats;
  private List<LifeTimeStat> lifeTimeStats;
  private List<RecentMatch> recentMatches;
  private String error;

  public FortniteAccount() {
  }

  public String getAccountId() {
    return accountId;
  }

  public int getPlatformId() {
    return platformId;
  }

  public String getPlatformName() {
    return platformName;
  }

  public String getPlatformNameLong() {
    return platformNameLong;
  }

  public String getEpicUserHandle() {
    return epicUserHandle;
  }

  public DivisionStats getStats() {
    return stats;
  }

  public List<LifeTimeStat> getLifeTimeStats() {
    return lifeTimeStats;
  }

  public List<RecentMatch> getRecentMatches() {
    return recentMatches;
  }

  public String getError() {
    return error;
  }

  public int getKills() {
    int kills = 0;
    for (LifeTimeStat stat : lifeTimeStats) {
      if (stat.getKey().equalsIgnoreCase("kills")) {
        kills = Integer.parseInt(stat.getValue());
      }
    }
    return kills;
  }

  public int getMatches() {
    int matches = 0;
    for (LifeTimeStat stat : lifeTimeStats) {
      if (stat.getKey().equalsIgnoreCase("matches played")) {
        matches = Integer.parseInt(stat.getValue());
      }
    }
    return matches;
  }

  public int getWins() {
    int wins = 0;
    for (LifeTimeStat stat : lifeTimeStats) {
      if (stat.getKey().equalsIgnoreCase("wins")) {
        wins = Integer.parseInt(stat.getValue());
      }
    }
    return wins;
  }

  @Override
  public String toString() {
    StringBuilder response = new StringBuilder(epicUserHandle + ":\n");

    response.append("\nLifetime Stats: \n");
    for (LifeTimeStat stat : lifeTimeStats) {
      if (!stat.getKey().contains("Top")) {
        response.append("\t").append(stat).append("\n");
      }
    }

    response.append("\nSolos: \n");
    response.append(stats.getP2()).append("\n");

    response.append("\nDuos: \n");
    response.append(stats.getP10()).append("\n");

    response.append("\nSquads: \n");
    response.append(stats.getP9());

    return response.toString();
  }
}
