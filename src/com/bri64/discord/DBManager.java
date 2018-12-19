package com.bri64.discord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.TimerTask;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DBManager {

  private final static String URL = "milkbukkit.io:3306";
  private final static String HOST =
      "jdbc:mysql://" + URL + "?serverTimezone=EST&autoReconnect=true";
  private final static String USER = "root";
  private final static String PASS = System.getenv("DB_PASS");
  private DiscordBot main;
  private Connection conn;

  public DBManager(DiscordBot main) {
    this.main = main;
  }

  // Connection methods
  public void start() {
    connect();
    keepAlive();
  }

  private void connect() {
    try {
      TimeZone timeZone = TimeZone.getTimeZone("America/New_York"); // e.g. "Europe/Rome"
      TimeZone.setDefault(timeZone);
      conn = DriverManager.getConnection(HOST, USER, PASS);
      BotUtils.log(main, "SQL Database connected!");
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private void disconnect() {
    try {
      if (conn != null) {
        conn.close();
        BotUtils.log(main, "SQL Database disconnected.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void reconnect() {
    BotUtils.log(main, "SQL Database error!");
    disconnect();
    connect();
  }

  private void keepAlive() {
    new java.util.Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          isAdmin("bri64#9180");
        } catch (SQLException e) {
          reconnect();
        }
      }
    }, 0L, 600000L);
  }

  public void stop() {
    disconnect();
  }

  // Data methods
  public String[] getCommandData(String command) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
        "SELECT * FROM `DankBot`.`Commands` WHERE Command=?;"
    );
    stmt.setString(1, command);
    ResultSet rs = stmt.executeQuery();

    String[] result = null;

    if (rs.next()) {
      result = new String[2];
      result[0] = rs.getString("URL");
      result[1] = rs.getString("Status");
    }

    return result;
  }

  public void addCommand(String command, String url) throws SQLException {
    String status = "[deprecated]";
    PreparedStatement stmt = conn.prepareStatement(
        "INSERT INTO `DankBot`.`Commands`\n" +
            "  (Command, URL, Status)\n" +
            "VALUES (?, ?, ?)\n" +
            "ON DUPLICATE KEY UPDATE\n" +
            "  Command      = VALUES(Command),\n" +
            "  URL          = VALUES(URL),\n" +
            "  Status       = VALUES(Status)"
    );
    stmt.setString(1, command);
    stmt.setString(2, url);
    stmt.setString(3, status);
    stmt.execute();
  }

  public void setJoin(String user, String joinURL) throws SQLException {
    String[] data = getJoinLeave(user);
    String leaveURL = "http://brianstrains.com/train3.mp3";
    if (data != null) {
      leaveURL = data[1];
    }
    PreparedStatement stmt = conn.prepareStatement(
        "INSERT INTO `DankBot`.`JoinLeave`\n" +
            "  (User, JoinURL, LeaveURL)\n" +
            "VALUES (?, ?, ?)\n" +
            "ON DUPLICATE KEY UPDATE\n" +
            "  User         = VALUES(User),\n" +
            "  JoinURL      = VALUES(JoinURL),\n" +
            "  LeaveURL     = VALUES(LeaveURL)"
    );
    stmt.setString(1, user);
    stmt.setString(2, joinURL);
    stmt.setString(3, leaveURL);
    stmt.execute();
  }

  public void setLeave(String user, String leaveURL) throws SQLException {
    String[] data = getJoinLeave(user);
    String joinURL = "http://brianstrains.com/train2.mp3";
    if (data != null) {
      joinURL = data[0];
    }
    PreparedStatement stmt = conn.prepareStatement(
        "INSERT INTO `DankBot`.`JoinLeave`\n" +
            "  (User, LeaveURL, JoinURL)\n" +
            "VALUES (?, ?, ?)\n" +
            "ON DUPLICATE KEY UPDATE\n" +
            "  User         = VALUES(User),\n" +
            "  LeaveURL     = VALUES(LeaveURL),\n" +
            "  JoinURL      = VALUES(JoinURL)"
    );
    stmt.setString(1, user);
    stmt.setString(2, leaveURL);
    stmt.setString(3, joinURL);
    stmt.execute();
  }

  public String[] getJoinLeave(String user) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
        "SELECT * FROM `DankBot`.`JoinLeave` WHERE User=?;"
    );
    stmt.setString(1, user);
    ResultSet rs = stmt.executeQuery();

    String[] result = null;

    if (rs.next()) {
      result = new String[2];
      result[0] = rs.getString("JoinURL");
      result[1] = rs.getString("LeaveURL");
    }

    return result;
  }

  public boolean isAdmin(String user) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(
        "SELECT * FROM `DankBot`.`Admins` WHERE User=?;"
    );
    stmt.setString(1, user);
    ResultSet rs = stmt.executeQuery();
    return rs.next();
  }

}
