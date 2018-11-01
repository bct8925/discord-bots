package com.bri64.discord.commands;

public class DiscordCommand extends AbstractCommand {

  protected boolean force;

  protected DiscordCommand(CommandEvent event, boolean force) {
    super(event);
    this.force = force;
  }

  @Override
  public void execute() {
  }

  @Override
  public void valid() {
  }

  @Override
  public void invalidArgs() {
  }
}
