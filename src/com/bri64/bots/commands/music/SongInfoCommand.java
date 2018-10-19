package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.MusicScheduler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;

public class SongInfoCommand extends MusicCommand {

  public SongInfoCommand(final MessageEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    BotUtils.sendMessage(user.mention() + " " + scheduler.getTrackInfo(),
        user.getOrCreatePMChannel());
  }
}
