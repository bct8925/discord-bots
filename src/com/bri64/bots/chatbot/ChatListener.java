package com.bri64.bots.chatbot;

import com.bri64.bots.DBManager;
import com.bri64.bots.audio.MusicScheduler;
import com.bri64.bots.commands.CoinCommand;
import com.bri64.bots.commands.HelpCommand;
import com.bri64.bots.commands.InvalidCommand;
import com.bri64.bots.commands.db.JoinLeaveDBCommand;
import com.bri64.bots.commands.music.KillCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

@SuppressWarnings("WeakerAccess")
public class ChatListener {

  private static String help = "```" +
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
    this.bot = bot;
    this.scheduler = scheduler;
    this.database = database;
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
    new HelpCommand(event, help).execute();
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    // Setup variables
    String message = event.getMessage().getContent();
    String command = message.split(" ")[0];

    // Coin
    if (command.equalsIgnoreCase(bot.getSymbol() + "coin")) {
      new CoinCommand(event).execute();
    }

    // Set Join
    else if (command.equalsIgnoreCase(bot.getSymbol() + "join")) {
      new JoinLeaveDBCommand(event, database, true).execute();
    }

    // Set Leave
    else if (command.equalsIgnoreCase(bot.getSymbol() + "leave")) {
      new JoinLeaveDBCommand(event, database, false).execute();
    }

    // Kill
    else if (message.equalsIgnoreCase(bot.getSymbol() + "kill")) {
      new KillCommand(event, scheduler).execute();
    }

    // Else
    else if (message.matches("^[" + bot.getSymbol() + "][^" + bot.getSymbol() + "].*")) {
      new InvalidCommand(event).execute();
    }
  }
}