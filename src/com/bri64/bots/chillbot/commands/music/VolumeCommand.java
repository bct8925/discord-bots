package com.bri64.bots.chillbot.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class VolumeCommand extends MusicCommand {

  public VolumeCommand(final MessageEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");

    // Argument check
    if (args.length != 2) {
      BotUtils.sendMessage(user.mention() + " " + "Invalid arguments! Usage: volume percent",
          user.getOrCreatePMChannel());
      return;
    }

    // Valid user check
    IGuild guild = !event.getChannel().isPrivate() ? event.getChannel().getGuild() : null;
    if (BotUtils.getConnectedChannel(guild, user) == null) {
      BotUtils.sendMessage(user.mention() + " " +
              "Error, can only be run from guild while user is in a voice channel.",
          user.getOrCreatePMChannel());
      return;
    }

    try {
      int percent = Integer.parseInt(args[1]);
      if (percent > 0 && percent <= 50) {
        scheduler.setVolume(percent);
      } else {
        BotUtils.sendMessage(user.mention() + " " + "Percent must be a number 1-50!",
            user.getOrCreatePMChannel());
      }
    } catch (NumberFormatException ex) {
      BotUtils.sendMessage(user.mention() + " " + "Percent must be a number 1-50!",
          user.getOrCreatePMChannel());
    }
  }
}
