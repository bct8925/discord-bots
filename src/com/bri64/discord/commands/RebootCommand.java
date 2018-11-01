package com.bri64.discord.commands;

import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.music.MusicCommand;

public class RebootCommand extends MusicCommand {

  public RebootCommand(CommandEvent event,
      MusicScheduler scheduler) {
    super(event, scheduler, false);
  }


  @Override
  public void execute() {
    System.out.println("Rebooting system...");
    scheduler.pause(true);
    scheduler.initAudio();
    scheduler.pause(false);
    System.out.println("Rebooted!");
  }

}
