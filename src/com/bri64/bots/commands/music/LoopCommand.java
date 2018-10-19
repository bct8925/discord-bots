package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.LoopMode;
import com.bri64.bots.audio.MusicScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class LoopCommand extends MusicCommand {

  public LoopCommand(final MessageEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");

    // Argument check
    if (args.length != 2) {
      BotUtils.sendMessage(user.mention() + " " + "Invalid arguments! Usage: loop none|one|all",
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

    String mode = message.split(" ")[1];
    switch (mode.toLowerCase()) {
      case "none":
        scheduler.setLoop(LoopMode.NONE);
        break;
      case "one":
        scheduler.setLoop(LoopMode.ONE);
        break;
      case "all":
        scheduler.setLoop(LoopMode.ALL);
        break;
      default:
        BotUtils.sendMessage(user.mention() + " " + "Invalid argument! Usage: loop none|one|all",
            user.getOrCreatePMChannel());
        break;
    }
  }
}
