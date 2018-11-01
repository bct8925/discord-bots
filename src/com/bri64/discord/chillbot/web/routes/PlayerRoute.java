package com.bri64.discord.chillbot.web.routes;

import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.send.MusicScheduler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateEngine;

@SuppressWarnings("WeakerAccess")
public abstract class PlayerRoute implements Route {

  protected DiscordBot bot;
  protected MusicScheduler scheduler;
  protected TemplateEngine templateEngine;

  public PlayerRoute(final DiscordBot bot, final MusicScheduler scheduler,
      final TemplateEngine templateEngine) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.templateEngine = templateEngine;
  }

  @Override
  public abstract Object handle(Request request, Response response) throws Exception;
}
