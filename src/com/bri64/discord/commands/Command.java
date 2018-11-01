package com.bri64.discord.commands;

public interface Command {

  void execute();

  void valid();

  void invalidArgs();
}
