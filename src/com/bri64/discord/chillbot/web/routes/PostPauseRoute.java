package com.bri64.discord.chillbot.web.routes;

import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.chillbot.web.WebPlayer;
import com.bri64.discord.commands.music.PauseCommand;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

@SuppressWarnings("RedundantThrows")
public class PostPauseRoute extends PlayerRoute {

  public PostPauseRoute(DiscordBot bot,
      MusicScheduler scheduler,
      TemplateEngine templateEngine) {
    super(bot, scheduler, templateEngine);
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    new PauseCommand(null, scheduler, true).execute();
    response.redirect(WebPlayer.HOME_URL);
    return null;
  }
}
