package com.bri64.bots.commands;

public interface Command {

  void execute();

  void valid();

  void invalidArgs();
}
