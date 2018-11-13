package com.bri64.discord.chatbot;

import com.bri64.discord.DBManager;
import com.bri64.discord.MessageListener;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CoinCommand;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.db.JoinLeaveDBCommand;
import com.bri64.discord.commands.error.InvalidCommandError;
import com.bri64.discord.commands.music.KillCommand;
import sx.blah.discord.api.events.EventSubscriber;

@SuppressWarnings("WeakerAccess")
public class ChatListener extends MessageListener {

  private static String CHATBOT_HELP = "```" +
      "ChatBot:\n" +
      "\t@ChatBot = Help Command\n" +
      "\t'coin' = Tosses a coin\n" +
      "\t'join url' = Change join audio\n" +
      "\t'leave url' = Chnage leave audio\n" +
      "\t'kill' = Kick ChatBot from channel" +
      "```";
  private ChatBot bot;
  private MusicScheduler scheduler;
  private DBManager database;

  public ChatListener(final ChatBot bot, final MusicScheduler scheduler,
      final DBManager database) {
    super(bot, CHATBOT_HELP);
    this.bot = bot;
    this.scheduler = scheduler;
    this.database = database;
  }

  @Override
  @EventSubscriber
  public void onCommand(CommandEvent event) {
    String command = event.getMessage().split(" ")[0];

    // Coin
    if (command.equalsIgnoreCase(bot.getSymbol() + "coin")) {
      new CoinCommand(event).execute();
    }

    // Set Join
    else if (command.equalsIgnoreCase(bot.getSymbol() + "join")) {
      new JoinLeaveDBCommand(event, scheduler, database, true).execute();
    }

    // Set Leave
    else if (command.equalsIgnoreCase(bot.getSymbol() + "leave")) {
      new JoinLeaveDBCommand(event, scheduler, database, false).execute();
    }

    // Kill
    else if (command.equalsIgnoreCase(bot.getSymbol() + "kill")) {
      new KillCommand(event, scheduler).execute();
    }

    // Else
    else if (event.getMessage().matches("^[" + bot.getSymbol() + "][^" + bot.getSymbol() + "].*")) {
      new InvalidCommandError(event).execute();
    }
  }
}