package com.bri64.bots.chillbot.commands;

import com.bri64.bots.BotUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("FieldCanBeLocal")
public class HelpCommand extends MessageCommand {

  private static String help = "```" +
      "ChillBot:\n" +
      "\t@ChillBot = Help Command\n" +
      "\t'play url' = Play the audio at the specified url now\n" +
      "\t'queue url' = Queue the audio at the specified url\n" +
      "\t'pause' = Pause/unpause the current track\n" +
      "\t'song' = Show info for currently playing track\n" +
      "\t'playlist' = Show info for current playlist\n" +
      "\t'next|skip' = Skip to next track in queue\n" +
      "\t'prev' = Skip to previous track in queue\n" +
      "\t'seek query' = Skip to next track whose title contains query\n" +
      "\t'loop mode' = Toggle loop mode [none, one, all]\n" +
      "\t'shuffle [on|off]' = Toggle queue shuffling or shuffle\n" +
      "\t'remove' = Remove current track from queue\n" +
      "\t'volume percent' = Change volume percent (1-50)\n" +
      "\t'add command url' = [Admin] Add a new command to the database\n" +
      "\t'kill' = Clear queue and kick ChillBot from channel" +
      "```";

  public HelpCommand(final MessageEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();

    if (!message.contains("@everyone") && !message.contains("@here")) {
      BotUtils.sendMessage(user.mention() + " " + help, user.getOrCreatePMChannel());
    }
  }
}
