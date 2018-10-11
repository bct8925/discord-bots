package com.bri64.bots.chillbot.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class ShuffleCommand extends MusicCommand {

  public ShuffleCommand(final MessageEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");

    // Argument check
    if (args.length > 2) {
      BotUtils.sendMessage(user.mention() + " " + "Invalid arguments! Usage: shuffle [on|off]",
          user.getOrCreatePMChannel());
    }

    // Valid user check
    IGuild guild = !event.getChannel().isPrivate() ? event.getChannel().getGuild() : null;
    if (BotUtils.getConnectedChannel(guild, user) == null) {
      BotUtils.sendMessage(user.mention() + " " +
              "Error, can only be run from guild while user is in a voice channel.",
          user.getOrCreatePMChannel());
      return;
    }

    String mode = (args.length > 1) ? message.split(" ")[1] : null;
    if (mode != null) {
      switch (mode.toLowerCase()) {
        case "on":
          scheduler.setShuffle(true);
          break;
        case "off":
          scheduler.setShuffle(false);
          break;
        default:
          BotUtils.sendMessage(user.mention() + " " + "Invalid argument! Usage: shuffle [on|off]",
              user.getOrCreatePMChannel());
          break;
      }
    } else {
      scheduler.shuffle();
    }
  }
}
