package com.bri64.bots.fortbot;

import com.bri64.bots.fortbot.json.FortniteAccount;

public class Player {

  private String username;
  private int kills;
  private int matches;
  private int wins;

  public Player(final String username, FortniteAccount account) {
    this.username = username;
    update(account);
  }

  public String getUsername() {
    return username;
  }

  public int getKills() {
    return kills;
  }

  public int getMatches() {
    return matches;
  }

  public int getWins() {
    return wins;
  }

  public boolean matchHappened(FortniteAccount account) {
    return account.getMatches() != matches;
  }

  public boolean winHappened(FortniteAccount account) {
    return account.getWins() != wins;
  }

  public int getNewKills(FortniteAccount account) {
    return account.getKills() - kills;
  }

  public int getNewMatches(FortniteAccount account) {
    return account.getKills() - kills;
  }

  public int getNewWins(FortniteAccount account) {
    return account.getKills() - kills;
  }

  public void update(FortniteAccount account) {
    this.kills = account.getKills();
    this.matches = account.getMatches();
    this.wins = account.getWins();
  }
}
